package controller;

import action.LoginActions;
import action.LoginViewContract;
import action.ResetPasswordData;
import model.Abbonamento;
import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LoginException;
import service.LoginService;
import service.LoginServiceIf;
import service.PasswordResetException;
import view.HomeView;
import view.RegistrazioneView;
import view.SelezionaAbbonamentoView;

public class LoginController implements LoginActions {

    private static final Logger logger =
            LogManager.getLogger(LoginController.class);

    private final LoginViewContract view;
    private final LoginServiceIf service;

    public LoginController(LoginViewContract view) {
        this.view = view;
        this.service = new LoginService();
        this.view.setController(this);
    }

    // ====================== LOGIN ======================
    @Override
    public void handleLogin(String username, String password) {
        logger.info("Tentativo di login per username: {}", username);

        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            view.mostraMessaggioErrore("Inserire sia username che password.");
            return;
        }

        try {
            Cliente cliente = service.autentica(username, password);
            Abbonamento abb = cliente.getAbbonamento();

            if (abb != null) {
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

        } catch (LoginException e) {
            view.mostraMessaggioErrore(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore inatteso durante il login di {}", username, e);
            view.mostraMessaggioErrore("Errore di sistema durante il login. Riprova più tardi.");
        }
    }

    // ====================== PASSWORD DIMENTICATA ======================
    @Override
    public void handlePasswordDimenticata() {

        String email = view.chiediEmailReset();
        if (email == null) return;

        email = email.trim().toLowerCase();
        if (email.isEmpty()) {
            view.mostraMessaggioErrore("Inserire un'email.");
            return;
        }

        String codiceGenerato;
        try {
            codiceGenerato = service.creaTokenReset(email);
        } catch (PasswordResetException e) {
            view.mostraMessaggioErrore(e.getMessage());
            return;
        }

        StringBuilder msg = new StringBuilder(
                "Ti è stato inviato un codice di reset all'email indicata.\n");
        if (codiceGenerato != null) {
            msg.append("(In ambiente di sviluppo, il codice è: ")
               .append(codiceGenerato)
               .append(")");
        }
        view.mostraMessaggioInfo(msg.toString());

        ResetPasswordData data = view.chiediCodiceENuovaPassword();
        if (data == null) return;

        String codiceInserito = data.codice != null ? data.codice.trim() : "";
        String nuovaPass      = data.nuovaPassword != null ? data.nuovaPassword : "";
        String confermaPass   = data.confermaPassword != null ? data.confermaPassword : "";

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

        try {
            service.resetPasswordConToken(codiceInserito, nuovaPass);
            view.mostraMessaggioInfo(
                    "Password aggiornata con successo. Ora puoi effettuare il login.");
        } catch (PasswordResetException e) {
            view.mostraMessaggioErrore(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore inatteso durante il reset password", e);
            view.mostraMessaggioErrore(
                    "Errore di sistema durante il reset della password. Riprova più tardi.");
        }
    }

    // ====================== REGISTRAZIONE ======================
    @Override
    public void handleRegistrazione() {
        logger.info("Apertura finestra di registrazione utente");

        view.dispose();

        RegistrazioneView regView = new RegistrazioneView();
        new RegistrazioneController(regView);
        regView.setVisible(true);
    }
}
