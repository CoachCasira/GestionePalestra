package controller;

import DB.GestioneDB;
import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.LoginView;
import view.RegistrazioneView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        this.view.setController(this);
    }

    // Gestisce il click su "Accedi"
    public void handleLogin(String username, String password) {
        logger.info("Tentativo di login per username: {}", username);

        if (username.isEmpty() || password.isEmpty()) {
            view.mostraMessaggioErrore("Inserire sia username che password.");
            return;
        }

        String sql = "SELECT * FROM CLIENTE WHERE USERNAME = ? AND PASSWORD = ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Creiamo l'oggetto Cliente a partire dai dati nel DB
                    Cliente clienteLoggato = creaClienteDaResultSet(rs);

                    view.mostraMessaggioInfo("Login effettuato con successo!");
                    logger.info("Login riuscito per {}", clienteLoggato.getUsername());

                    // TODO: apri la schermata principale dell'applicazione
                    // passando 'clienteLoggato' al prossimo controller/view

                } else {
                    view.mostraMessaggioErrore("Credenziali non valide.");
                    logger.warn("Login fallito per {}", username);
                }
            }

        } catch (SQLException e) {
            logger.error("Errore durante il login di {}", username, e);
            view.mostraMessaggioErrore("Errore di database durante il login.");
        }
    }

    // Gestisce il click su "Registrati"
    public void handleRegistrazione() {
        logger.info("Apertura finestra di registrazione utente");

        view.mostraMessaggioInfo("Apertura schermata di registrazione...");

        RegistrazioneView regView = new RegistrazioneView();
        new RegistrazioneController(regView); // collega controller e view
        regView.setVisible(true);
    }

    // --------- Metodo di supporto per costruire l'oggetto Cliente ---------

    private Cliente creaClienteDaResultSet(ResultSet rs) throws SQLException {
        String username = rs.getString("USERNAME");
        String password = rs.getString("PASSWORD");
        String nome = rs.getString("NOME");
        String cognome = rs.getString("COGNOME");
        String cf = rs.getString("CF");
        String luogoNascita = rs.getString("LUOGO_NASCITA");
        java.util.Date dataNascita = rs.getDate("DATA_NASCITA"); // java.sql.Date è una java.util.Date
        String iban = rs.getString("IBAN");

        return new Cliente(
                username,
                password,
                nome,
                cognome,
                cf,
                luogoNascita,
                dataNascita,
                iban
        );
    }
}
