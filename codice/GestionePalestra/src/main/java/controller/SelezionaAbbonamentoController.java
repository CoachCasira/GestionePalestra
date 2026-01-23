package controller;

import action.SelezionaAbbonamentoActions;
import action.SelezionaAbbonamentoViewContract;
import model.Abbonamento;
import model.Cliente;
import view.LoginView;
import view.PagamentoView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelezionaAbbonamentoController implements SelezionaAbbonamentoActions {

    private static final Logger logger =
            LogManager.getLogger(SelezionaAbbonamentoController.class);

    private final SelezionaAbbonamentoViewContract view;
    private final Cliente cliente;

    public SelezionaAbbonamentoController(SelezionaAbbonamentoViewContract view,
                                          Cliente cliente) {
        this.view = view;
        this.cliente = cliente;
        this.view.setActions(this);
    }

    @Override
    public void onProcedi(String tipoAbbonamento) {
        logger.info("Utente {} ha scelto abbonamento {}",
                cliente.getUsername(), tipoAbbonamento);

        Abbonamento abb = Abbonamento.creaDaTipo(tipoAbbonamento, 0);

        PagamentoView pView = new PagamentoView(cliente, abb);
        new PagamentoController(pView, cliente, abb);
        pView.setVisible(true);

        view.close();
    }

    @Override
    public void onAnnulla() {
        view.close();

        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}
