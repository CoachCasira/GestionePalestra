package controller;

import DB.GestioneDB;
import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.RegistrazioneView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // java.sql.Date

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
                               String dataNascita,  // come stringa "yyyy-MM-dd"
                               String iban) {

        // 1) Validazione minima
        if (username.isEmpty() || password.isEmpty() ||
                nome.isEmpty() || cognome.isEmpty() || cf.isEmpty()) {
            view.mostraMessaggioErrore(
                    "Compilare tutti i campi obbligatori (username, password, nome, cognome, CF).");
            return;
        }

        logger.info("Richiesta registrazione per username {}", username);

        // 2) Controllo se username o CF esistono già nel DB
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

        // 3) Parsing della data (stringa -> java.sql.Date)
        Date dataSql;
        try {
            dataSql = Date.valueOf(dataNascita); // formato "yyyy-MM-dd"
        } catch (IllegalArgumentException e) {
            logger.error("Formato data non valido: {}", dataNascita);
            view.mostraMessaggioErrore("Inserire la data nel formato corretto: yyyy-MM-dd");
            return;
        }

        // 4) Creazione dell'oggetto Cliente lato Java
        Cliente nuovoCliente = new Cliente(
                username,
                password,
                nome,
                cognome,
                cf,
                luogoNascita,
                dataSql,  // java.sql.Date estende java.util.Date, va bene per il costruttore
                iban
        );

        // 5) Insert su tabella CLIENTE usando i getter dell'oggetto
        String sql = "INSERT INTO CLIENTE " +
                "(USERNAME, NOME, COGNOME, CF, LUOGO_NASCITA, DATA_NASCITA, IBAN, PASSWORD) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuovoCliente.getUsername());
            ps.setString(2, nuovoCliente.getNome());
            ps.setString(3, nuovoCliente.getCognome());
            ps.setString(4, nuovoCliente.getCF());
            ps.setString(5, nuovoCliente.getLuogoNascita());
            ps.setDate  (6, new Date(nuovoCliente.getDataNascita().getTime()));
            ps.setString(7, nuovoCliente.getIban());
            ps.setString(8, nuovoCliente.getPassword());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                logger.info("Registrazione completata per {}", username);
                view.mostraMessaggioInfo("Registrazione completata con successo!");
                view.dispose();
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

    // ---------- Metodi di supporto per controlli preliminari ----------

    private boolean esisteUsername(String username) {
        String sql = "SELECT 1 FROM CLIENTE WHERE USERNAME = ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // se c'è almeno una riga → username esiste
            }

        } catch (SQLException e) {
            logger.error("Errore nel controllo esistenza username {}", username, e);
            return false; // per non bloccare tutto in caso di errore
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
}
