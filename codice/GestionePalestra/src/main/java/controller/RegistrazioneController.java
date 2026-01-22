package controller;

import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.RegistrazioneView;
import view.LoginView;
import service.RegistrazioneServiceIf;
import service.RegistrazioneService;
import service.RegistrazioneException;

public class RegistrazioneController {

    private static final Logger logger =
            LogManager.getLogger(RegistrazioneController.class);

    private final RegistrazioneView view;
    private final RegistrazioneServiceIf service;

    public RegistrazioneController(RegistrazioneView view) {
        this.view = view;
        this.service = new RegistrazioneService();
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

        try {
            service.registraNuovoCliente(username, password, nome, cognome,
                    cf, luogoNascita, dataNascita, iban, email);

            view.mostraMessaggioInfo("Registrazione completata con successo!");

            view.dispose();
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);

        } catch (RegistrazioneException e) {
            // errore “previsto” con messaggio per l’utente
            view.mostraMessaggioErrore(e.getMessage());
        } catch (Exception e) {
            // qualcosa di imprevisto
            logger.error("Errore inatteso durante la registrazione", e);
            view.mostraMessaggioErrore(
                    "Errore di sistema durante la registrazione. Riprova più tardi.");
        }
    }

    public void handleAnnulla() {
        logger.info("Registrazione annullata, ritorno alla schermata di login");

        view.dispose();

        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}
