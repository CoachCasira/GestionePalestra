package controller;

import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import db.GestioneDB;
import view.RegistrazioneView;
import view.LoginView;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

public class RegistrazioneController {

    private static final Logger logger =
            LogManager.getLogger(RegistrazioneController.class);

    private final RegistrazioneView view;

    public RegistrazioneController(RegistrazioneView view) {
        this.view = view;
        this.view.setController(this);
    }

    public void handleConferma(String username,
                               String password,
                               String nome,
                               String cognome,
                               String cf,
                               String luogoNascita,
                               String dataNascita,
                               String iban,
                               String email) {

        username     = username.trim();
        password     = password; // non trim, per non togliere spazi interni
        nome         = nome.trim();
        cognome      = cognome.trim();
        cf           = cf.trim().toUpperCase();
        luogoNascita = luogoNascita.trim();
        dataNascita  = dataNascita.trim();
        iban         = iban.trim().replaceAll("\\s+", "").toUpperCase();
        email        = email.trim().toLowerCase();

        if (username.isEmpty() || password.isEmpty() ||
                nome.isEmpty() || cognome.isEmpty() ||
                cf.isEmpty() || email.isEmpty()) {
            view.mostraMessaggioErrore(
                    "Compilare tutti i campi obbligatori (username, password, nome, cognome, CF, email).");
            return;
        }

        if (password.length() < 4) {
            view.mostraMessaggioErrore("La password deve contenere almeno 4 caratteri.");
            return;
        }

        if (!cf.matches("[A-Z0-9]{16}")) {
            view.mostraMessaggioErrore(
                    "Codice fiscale non valido. Deve contenere 16 caratteri alfanumerici.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            view.mostraMessaggioErrore("Formato email non valido.");
            return;
        }

        if (!iban.isEmpty() && !isValidIban(iban)) {
            view.mostraMessaggioErrore(
                    "IBAN non valido. Deve essere lungo tra 15 e 34 caratteri, solo lettere e numeri.");
            return;
        }

        if (!isValidCountry(luogoNascita)) {
            view.mostraMessaggioErrore(
                    "Il paese di nascita inserito non è valido. Inserire un paese reale (es. Italia, France, Spain...).");
            return;
        }

        logger.info("Richiesta registrazione per username {}", username);

        if (esisteUsername(username)) {
            view.mostraMessaggioErrore("Username già utilizzato, scegline un altro.");
            logger.warn("Registrazione fallita: username {} già esistente", username);
            return;
        }

        if (esisteCF(cf)) {
            view.mostraMessaggioErrore("Esiste già un cliente con questo codice fiscale.");
            logger.warn("Registrazione fallita: CF {} già esistente", cf);
            return;
        }

        if (esisteEmail(email)) {
            view.mostraMessaggioErrore("Esiste già un account associato a questa email.");
            logger.warn("Registrazione fallita: EMAIL {} già esistente", email);
            return;
        }

        Date dataSql;
        try {
            dataSql = Date.valueOf(dataNascita);
        } catch (IllegalArgumentException e) {
            logger.error("Formato data non valido: {}", dataNascita);
            view.mostraMessaggioErrore("Inserire la data nel formato corretto: yyyy-MM-dd");
            return;
        }

        LocalDate nascitaLD = dataSql.toLocalDate();
        LocalDate oggi      = LocalDate.now();

        if (nascitaLD.isAfter(oggi)) {
            view.mostraMessaggioErrore("La data di nascita non può essere nel futuro.");
            return;
        }

        int anni = Period.between(nascitaLD, oggi).getYears();
        if (anni < 14) {
            view.mostraMessaggioErrore("Per registrarsi bisogna avere almeno 14 anni.");
            return;
        }

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
                view.mostraMessaggioInfo("Registrazione completata con successo!");

                view.dispose();
                LoginView loginView = new LoginView();
                new LoginController(loginView);
                loginView.setVisible(true);

            } else {
                logger.warn("Nessuna riga inserita per {}", username);
                view.mostraMessaggioErrore(
                        "Si è verificato un problema durante la registrazione.");
            }

        } catch (SQLException e) {
            logger.error("Errore durante la registrazione utente", e);
            view.mostraMessaggioErrore(
                    "Errore di database durante la registrazione.");
        }
    }

    public void handleAnnulla() {
        logger.info("Registrazione annullata, ritorno alla schermata di login");

        view.dispose();

        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }

    private boolean esisteUsername(String username) {
        String sql = "SELECT 1 FROM CLIENTE WHERE USERNAME = ?";
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Errore nel controllo esistenza username {}", username, e);
            return false;
        }
    }

    private boolean esisteCF(String cf) {
        String sql = "SELECT 1 FROM CLIENTE WHERE CF = ?";
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Errore nel controllo esistenza CF {}", cf, e);
            return false;
        }
    }

    private boolean esisteEmail(String email) {
        String sql = "SELECT 1 FROM CLIENTE WHERE EMAIL = ?";
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Errore nel controllo esistenza EMAIL {}", email, e);
            return false;
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
