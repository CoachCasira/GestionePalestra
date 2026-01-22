package service;

import db.GestioneDB;
import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

public class RegistrazioneService implements RegistrazioneServiceIf {

    private static final Logger logger =
            LogManager.getLogger(RegistrazioneService.class);

    @Override
    public void registraNuovoCliente(String username,
                                     String password,
                                     String nome,
                                     String cognome,
                                     String cf,
                                     String luogoNascita,
                                     String dataNascita,
                                     String iban,
                                     String email) throws RegistrazioneException {

        // ===== Normalizzazione input =====
        username     = username.trim();
        password     = password; // non trim
        nome         = nome.trim();
        cognome      = cognome.trim();
        cf           = cf.trim().toUpperCase();
        luogoNascita = luogoNascita.trim();
        dataNascita  = dataNascita.trim();
        iban         = iban.trim().replaceAll("\\s+", "").toUpperCase();
        email        = email.trim().toLowerCase();

        // ===== Validazioni base =====
        if (username.isEmpty() || password.isEmpty() ||
                nome.isEmpty() || cognome.isEmpty() ||
                cf.isEmpty() || email.isEmpty()) {
            throw new RegistrazioneException(
                    "Compilare tutti i campi obbligatori (username, password, nome, cognome, CF, email).");
        }

        if (password.length() < 4) {
            throw new RegistrazioneException("La password deve contenere almeno 4 caratteri.");
        }

        if (!cf.matches("[A-Z0-9]{16}")) {
            throw new RegistrazioneException(
                    "Codice fiscale non valido. Deve contenere 16 caratteri alfanumerici.");
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RegistrazioneException("Formato email non valido.");
        }

        if (!iban.isEmpty() && !isValidIban(iban)) {
            throw new RegistrazioneException(
                    "IBAN non valido. Deve essere lungo tra 15 e 34 caratteri, solo lettere e numeri.");
        }

        if (!isValidCountry(luogoNascita)) {
            throw new RegistrazioneException(
                    "Il paese di nascita inserito non è valido. Inserire un paese reale (es. Italia, France, Spain...).");
        }

        logger.info("Richiesta registrazione per username {}", username);

        // ===== Controlli di unicità =====
        try {
            if (esisteUsername(username)) {
                logger.warn("Registrazione fallita: username {} già esistente", username);
                throw new RegistrazioneException("Username già utilizzato, scegline un altro.");
            }

            if (esisteCF(cf)) {
                logger.warn("Registrazione fallita: CF {} già esistente", cf);
                throw new RegistrazioneException("Esiste già un cliente con questo codice fiscale.");
            }

            if (esisteEmail(email)) {
                logger.warn("Registrazione fallita: EMAIL {} già esistente", email);
                throw new RegistrazioneException("Esiste già un account associato a questa email.");
            }

        } catch (SQLException e) {
            logger.error("Errore nel controllo unicità durante la registrazione", e);
            throw new RegistrazioneException("Errore di database durante i controlli di registrazione.", e);
        }

        // ===== Parsing data di nascita =====
        Date dataSql;
        try {
            dataSql = Date.valueOf(dataNascita);
        } catch (IllegalArgumentException e) {
            logger.error("Formato data non valido: {}", dataNascita);
            throw new RegistrazioneException("Inserire la data nel formato corretto: yyyy-MM-dd");
        }

        LocalDate nascitaLD = dataSql.toLocalDate();
        LocalDate oggi      = LocalDate.now();

        if (nascitaLD.isAfter(oggi)) {
            throw new RegistrazioneException("La data di nascita non può essere nel futuro.");
        }

        int anni = Period.between(nascitaLD, oggi).getYears();
        if (anni < 14) {
            throw new RegistrazioneException("Per registrarsi bisogna avere almeno 14 anni.");
        }

        // ===== Hash password + creazione cliente =====
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        Cliente nuovoCliente = new Cliente(
                username,
                hashedPassword,
                nome,
                cognome,
                cf,
                luogoNascita,
                dataSql,
                iban,
                email
        );

        // ===== Insert su DB =====
        String sql = "INSERT INTO CLIENTE " +
                "(USERNAME, EMAIL, NOME, COGNOME, CF, LUOGO_NASCITA, DATA_NASCITA, IBAN, PASSWORD) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuovoCliente.getUsername());
            ps.setString(2, nuovoCliente.getEmail());
            ps.setString(3, nuovoCliente.getNome());
            ps.setString(4, nuovoCliente.getCognome());
            ps.setString(5, nuovoCliente.getCF());
            ps.setString(6, nuovoCliente.getLuogoNascita());
            ps.setDate  (7, new Date(nuovoCliente.getDataNascita().getTime()));
            ps.setString(8, nuovoCliente.getIban());
            ps.setString(9, nuovoCliente.getPassword());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                logger.info("Registrazione completata per {}", username);
            } else {
                logger.warn("Nessuna riga inserita per {}", username);
                throw new RegistrazioneException(
                        "Si è verificato un problema durante la registrazione.");
            }

        } catch (SQLException e) {
            logger.error("Errore durante la registrazione utente", e);
            throw new RegistrazioneException("Errore di database durante la registrazione.", e);
        }
    }

    // ===== Metodi di supporto (spostati dal controller) =====

    private boolean esisteUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM CLIENTE WHERE USERNAME = ?";
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean esisteCF(String cf) throws SQLException {
        String sql = "SELECT 1 FROM CLIENTE WHERE CF = ?";
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean esisteEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM CLIENTE WHERE EMAIL = ?";
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean isValidIban(String iban) {
        int len = iban.length();
        if (len < 15 || len > 34) return false;
        return iban.matches("[A-Z0-9]+");
    }

    private boolean isValidCountry(String countryName) {
        if (countryName == null || countryName.isEmpty()) return false;
        String normalized = countryName.trim().toLowerCase();

        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            String it = l.getDisplayCountry(Locale.ITALIAN).toLowerCase();
            String en = l.getDisplayCountry(Locale.ENGLISH).toLowerCase();
            if (normalized.equals(it) || normalized.equals(en)) {
                return true;
            }
        }
        return false;
    }
}
