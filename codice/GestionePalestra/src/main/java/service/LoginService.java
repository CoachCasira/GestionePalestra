package service;

import db.GestioneDB;
import db.dao.AbbonamentoDAO;
import model.Abbonamento;
import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;

public class LoginService implements LoginServiceIf {

    private static final Logger logger =
            LogManager.getLogger(LoginService.class);

    @Override
    public Cliente autentica(String username, String password) throws LoginException {
        String sql = "SELECT * FROM CLIENTE WHERE USERNAME = ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("Login fallito (username inesistente) per {}", username);
                    throw new LoginException("Credenziali non valide.");
                }

                String storedHash = rs.getString("PASSWORD");

                if (storedHash == null || !BCrypt.checkpw(password, storedHash)) {
                    logger.warn("Login fallito (password errata) per {}", username);
                    throw new LoginException("Credenziali non valide.");
                }

                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("ID_CLIENTE"));
                cliente.setUsername(rs.getString("USERNAME"));
                cliente.setEmail(rs.getString("EMAIL"));
                cliente.setNome(rs.getString("NOME"));
                cliente.setCognome(rs.getString("COGNOME"));
                cliente.setCF(rs.getString("CF"));
                cliente.setLuogoNascita(rs.getString("LUOGO_NASCITA"));
                cliente.setPassword(storedHash); // hash
                cliente.setIban(rs.getString("IBAN"));
                Date dataNascita = rs.getDate("DATA_NASCITA");
                if (dataNascita != null) {
                    cliente.setDataNascita(new java.util.Date(dataNascita.getTime()));
                }

                // carico eventuale abbonamento
                Abbonamento abb = AbbonamentoDAO
                        .getAbbonamentoByClienteId(cliente.getIdCliente());
                if (abb != null) {
                    cliente.setAbbonamento(abb);
                }

                logger.info("Login riuscito per {}", username);
                return cliente;
            }

        } catch (SQLException e) {
            logger.error("Errore durante il login di {}", username, e);
            throw new LoginException("Errore di database durante il login.", e);
        }
    }

    @Override
    public String creaTokenReset(String email) throws PasswordResetException {
        int idCliente = -1;
        String codice = null;

        try (Connection conn = GestioneDB.getConnection()) {

            String sqlFind = "SELECT ID_CLIENTE FROM CLIENTE WHERE EMAIL = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlFind)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idCliente = rs.getInt("ID_CLIENTE");
                    }
                }
            }

            // per privacy non segnaliamo se l'email esiste o meno
            if (idCliente == -1) {
                return null; // nessun account associato, ma verso la view diciamo cmq lo stesso messaggio
            }

            codice = String.format("%06d", (int) (Math.random() * 1_000_000));
            LocalDateTime now = LocalDateTime.now();
            Timestamp scadenza = Timestamp.valueOf(now.plusMinutes(15));

            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PASSWORD_RESET_TOKEN WHERE ID_CLIENTE = ?")) {
                del.setInt(1, idCliente);
                del.executeUpdate();
            }

            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO PASSWORD_RESET_TOKEN (ID_CLIENTE, TOKEN, SCADENZA, UTILIZZATO) " +
                            "VALUES (?, ?, ?, FALSE)")) {
                ins.setInt(1, idCliente);
                ins.setString(2, codice);
                ins.setTimestamp(3, scadenza);
                ins.executeUpdate();
            }

            logger.info("Creato token reset password per email {}", email);
            return codice;

        } catch (SQLException e) {
            logger.error("Errore durante la generazione del token di reset per email {}", email, e);
            throw new PasswordResetException(
                    "Errore di database durante la richiesta di reset password.", e);
        }
    }

    @Override
    public void resetPasswordConToken(String codice, String nuovaPass)
            throws PasswordResetException {

        try (Connection conn = GestioneDB.getConnection()) {

            String sqlTok = "SELECT ID_CLIENTE, SCADENZA, UTILIZZATO " +
                    "FROM PASSWORD_RESET_TOKEN " +
                    "WHERE TOKEN = ? " +
                    "ORDER BY SCADENZA DESC " +
                    "LIMIT 1";

            int cliId = -1;
            Timestamp scad = null;
            boolean usato = false;

            try (PreparedStatement ps = conn.prepareStatement(sqlTok)) {
                ps.setString(1, codice);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cliId = rs.getInt("ID_CLIENTE");
                        scad  = rs.getTimestamp("SCADENZA");
                        usato = rs.getBoolean("UTILIZZATO");
                    }
                }
            }

            if (cliId == -1) {
                throw new PasswordResetException("Codice non valido.");
            }

            Timestamp nowTs = Timestamp.valueOf(LocalDateTime.now());
            if (scad.before(nowTs)) {
                throw new PasswordResetException("Il codice è scaduto. Richiedi un nuovo reset.");
            }

            if (usato) {
                throw new PasswordResetException("Questo codice è già stato utilizzato.");
            }

            // controllo che la nuova password sia diversa da quella attuale
            String storedHash = null;
            String sqlPwd = "SELECT PASSWORD FROM CLIENTE WHERE ID_CLIENTE = ?";

            try (PreparedStatement psPwd = conn.prepareStatement(sqlPwd)) {
                psPwd.setInt(1, cliId);
                try (ResultSet rs = psPwd.executeQuery()) {
                    if (rs.next()) {
                        storedHash = rs.getString("PASSWORD");
                    }
                }
            }

            if (storedHash != null && BCrypt.checkpw(nuovaPass, storedHash)) {
                throw new PasswordResetException(
                        "La nuova password non può essere uguale a quella attuale. " +
                        "Scegli una password diversa.");
            }

            // hash nuova password
            String newHash = BCrypt.hashpw(nuovaPass, BCrypt.gensalt(12));

            // aggiorno password
            try (PreparedStatement upd = conn.prepareStatement(
                    "UPDATE CLIENTE SET PASSWORD = ? WHERE ID_CLIENTE = ?")) {
                upd.setString(1, newHash);
                upd.setInt(2, cliId);
                upd.executeUpdate();
            }

            // segno token come usato
            try (PreparedStatement updTok = conn.prepareStatement(
                    "UPDATE PASSWORD_RESET_TOKEN SET UTILIZZATO = TRUE WHERE TOKEN = ?")) {
                updTok.setString(1, codice);
                updTok.executeUpdate();
            }

            logger.info("Password aggiornata tramite reset per cliente {}", cliId);

        } catch (SQLException e) {
            logger.error("Errore durante l'aggiornamento della password tramite reset", e);
            throw new PasswordResetException(
                    "Errore di database durante il reset della password.", e);
        }
    }
}
