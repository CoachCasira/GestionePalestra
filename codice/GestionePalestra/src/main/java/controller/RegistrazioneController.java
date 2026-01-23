package controller;

import action.RegistrazioneActions;
import action.RegistrazioneViewContract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.RegistrazioneException;
import service.RegistrazioneService;
import service.RegistrazioneServiceIf;
import view.LoginView;

public class RegistrazioneController implements RegistrazioneActions {

    private static final Logger logger =
            LogManager.getLogger(RegistrazioneController.class);

    private final RegistrazioneViewContract view;
    private final RegistrazioneServiceIf service;

    public RegistrazioneController(RegistrazioneViewContract view) {
        this.view = view;
        this.service = new RegistrazioneService();
        this.view.setActions(this);
    }

    @Override
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
            apriLogin();

        } catch (RegistrazioneException e) {
            view.mostraMessaggioErrore(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore inatteso durante la registrazione", e);
            view.mostraMessaggioErrore(
                    "Errore di sistema durante la registrazione. Riprova più tardi.");
        }
    }

    private void apriLogin() {
        view.close();

        LoginView loginView = new LoginView();
        new LoginController(loginView);   // LoginController accetta il contract (LoginView implementa il contract)
        loginView.setVisible(true);
    }
    @Override
    public void handleAnnulla() {
        logger.info("Registrazione annullata, ritorno alla schermata di login");
        apriLogin();
    }
}
