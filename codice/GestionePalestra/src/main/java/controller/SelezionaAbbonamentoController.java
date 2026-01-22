package controller;

import model.Abbonamento;
import model.Cliente;
import view.SelezionaAbbonamentoView;
import view.LoginView;
import view.PagamentoView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelezionaAbbonamentoController {

    private static final Logger logger =
            LogManager.getLogger(SelezionaAbbonamentoController.class);

    private final SelezionaAbbonamentoView view;
    private final Cliente cliente;

    public SelezionaAbbonamentoController(SelezionaAbbonamentoView view,
                                          Cliente cliente) {
        this.view = view;
        this.cliente = cliente;
        this.view.setController(this);
    }

    /** L’utente ha scelto un tipo di abbonamento e vuole procedere al pagamento. */
    public void handleProcedi(String tipoAbbonamento) {
        logger.info("Utente {} ha scelto abbonamento {}",
                cliente.getUsername(), tipoAbbonamento);

        // creiamo l'abbonamento "in memoria" (non ancora salvato su DB)
        Abbonamento abb = Abbonamento.creaDaTipo(tipoAbbonamento, 0);

        // apriamo la schermata di pagamento
        PagamentoView pView = new PagamentoView(cliente, abb);
        new PagamentoController(pView, cliente, abb);
        pView.setVisible(true);

        view.dispose();
    }

    public void handleAnnulla() {
        view.dispose();
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);
        loginView.setVisible(true);
    }
}
