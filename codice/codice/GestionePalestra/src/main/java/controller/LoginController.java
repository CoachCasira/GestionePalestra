package controller;

import db.GestioneDB;
import db.dao.AbbonamentoDAO;
import model.Abbonamento;
import model.Cliente;
import view.HomeView;
import view.LoginView;
import view.RegistrazioneView;
import view.SelezionaAbbonamentoView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;

import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    private static final Logger logger =
            LogManager.getLogger(LoginController.class);

    private final LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        this.view.setController(this);
    }

    // ====================== LOGIN ======================
    public void handleLogin(String username, String password) {
        logger.info("Tentativo di login per username: {}", username);

        if (username.isEmpty() || password.isEmpty()) {
            view.mostraMessaggioErrore("Inserire sia username che password.");
            return;
        }

        String sql = "SELECT * FROM CLIENTE WHERE USERNAME = ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    view.mostraMessaggioErrore("Credenziali non valide.");
                    logger.warn("Login fallito (username inesistente) per {}", username);
                    return;
                }

                String storedHash = rs.getString("PASSWORD");

                if (storedHash == null || !BCrypt.checkpw(password, storedHash)) {
                    view.mostraMessaggioErrore("Credenziali non valide.");
                    logger.warn("Login fallito (password errata) per {}", username);
                    return;
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

                logger.info("Login riuscito per {}", username);

                Abbonamento abb = AbbonamentoDAO.getAbbonamentoByClienteId(cliente.getIdCliente());

                if (abb != null) {
                    cliente.setAbbonamento(abb);
                    view.mostraMessaggioInfo("Benvenuto, accesso effettuato. Abbonamento attivo trovato.");
                    view.dispose();

                    HomeView hView = new HomeView(cliente);
                    new HomeController(hView, cliente);
                    hView.setVisible(true);

                } else {
                    view.mostraMessaggioInfo("Accesso effettuato. Nessun abbonamento attivo, selezionane uno.");
                    view.dispose();

                    SelezionaAbbonamentoView sView = new SelezionaAbbonamentoView(cliente);
                    new SelezionaAbbonamentoController(sView, cliente);
                    sView.setVisible(true);
                }

            }

        } catch (SQLException e) {
            logger.error("Errore durante il login di {}", username, e);
            view.mostraMessaggioErrore("Errore di database durante il login.");
        }
    }

    // ====================== PASSWORD DIMENTICATA ======================
    /**
     * Flusso "Password dimenticata":
     * 1) chiede email alla view
     * 2) genera token, lo salva su PASSWORD_RESET_TOKEN
     * 3) mostra info con codice (in ambiente dev)
     * 4) chiede codice + nuova password
     * 5) verifica token e aggiorna password hashata
     */
    public void handlePasswordDimenticata() {

        // 1) chiedo email alla view
        String email = view.chiediEmailReset();
        if (email == null) {
            return; // annullato
        }
        email = email.trim().toLowerCase();
        if (email.isEmpty()) {
            view.mostraMessaggioErrore("Inserire un'email.");
            return;
        }

        int idCliente = -1;
        String codice;

        // 2) generazione token e salvataggio su PASSWORD_RESET_TOKEN
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

            // per privacy non diciamo se l'email esiste o meno
            if (idCliente == -1) {
                view.mostraMessaggioInfo(
                        "Se esiste un account associato a questa email, riceverai un codice di reset.");
                return;
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

            // TODO: invio email reale
            view.mostraMessaggioInfo(
                    "Ti è stato inviato un codice di reset all'email indicata.\n" +
                            "(In ambiente di sviluppo, il codice è: " + codice + ")");

        } catch (SQLException e) {
            logger.error("Errore durante la generazione del token di reset per email {}", email, e);
            view.mostraMessaggioErrore("Errore di database durante la richiesta di reset password.");
            return;
        }

        // 3) chiedo codice + nuova password alla view
        LoginView.ResetPasswordData data = view.chiediCodiceENuovaPassword();
        if (data == null) {
            return; // annullato
        }

        String codiceInserito = data.codice;
        String nuovaPass      = data.nuovaPassword;
        String confermaPass   = data.confermaPassword;

        if (codiceInserito.isEmpty() || nuovaPass.isEmpty() || confermaPass.isEmpty()) {
            view.mostraMessaggioErrore("Tutti i campi sono obbligatori.");
            return;
        }
        if (!nuovaPass.equals(confermaPass)) {
            view.mostraMessaggioErrore("Le due password non coincidono.");
            return;
        }
        if (nuovaPass.length() < 4) {
            view.mostraMessaggioErrore("La nuova password deve contenere almeno 4 caratteri.");
            return;
        }

        // 4) verifica token e aggiornamento password
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
                ps.setString(1, codiceInserito);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cliId = rs.getInt("ID_CLIENTE");
                        scad  = rs.getTimestamp("SCADENZA");
                        usato = rs.getBoolean("UTILIZZATO");
                    }
                }
            }

            if (cliId == -1) {
                view.mostraMessaggioErrore("Codice non valido.");
                return;
            }

            Timestamp nowTs = Timestamp.valueOf(LocalDateTime.now());
            if (scad.before(nowTs)) {
                view.mostraMessaggioErrore("Il codice è scaduto. Richiedi un nuovo reset.");
                return;
            }

            if (usato) {
                view.mostraMessaggioErrore("Questo codice è già stato utilizzato.");
                return;
            }

            // *** NUOVO CONTROLLO: la nuova password non deve coincidere con quella attuale ***
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
                view.mostraMessaggioErrore(
                        "La nuova password non può essere uguale a quella attuale. " +
                        "Scegli una password diversa.");
                return;
            }
            // *** FINE NUOVO CONTROLLO ***

            // 4.a) Hash della nuova password
            String newHash = BCrypt.hashpw(nuovaPass, BCrypt.gensalt(12));

            // 4.b) Aggiorno la password del cliente
            try (PreparedStatement upd = conn.prepareStatement(
                    "UPDATE CLIENTE SET PASSWORD = ? WHERE ID_CLIENTE = ?")) {
                upd.setString(1, newHash);
                upd.setInt(2, cliId);
                upd.executeUpdate();
            }

            // 4.c) Segno il token come utilizzato
            try (PreparedStatement updTok = conn.prepareStatement(
                    "UPDATE PASSWORD_RESET_TOKEN SET UTILIZZATO = TRUE WHERE TOKEN = ?")) {
                updTok.setString(1, codiceInserito);
                updTok.executeUpdate();
            }

            view.mostraMessaggioInfo("Password aggiornata con successo. Ora puoi effettuare il login.");

        } catch (SQLException e) {
            logger.error("Errore durante l'aggiornamento della password tramite reset", e);
            view.mostraMessaggioErrore("Errore di database durante il reset della password.");
        }
    }

    // ====================== REGISTRAZIONE ======================
    public void handleRegistrazione() {
        logger.info("Apertura finestra di registrazione utente");

        view.dispose();

        RegistrazioneView regView = new RegistrazioneView();
        new RegistrazioneController(regView);
        regView.setVisible(true);
    }
}
