package controller;

import action.PagamentoAction;
import action.PagamentoViewContract;
import db.dao.AbbonamentoDAO;
import db.dao.PagamentoDAO;
import model.Abbonamento;
import model.Cliente;
import model.Pagamento;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.HomeView;

import java.util.Date;

public class PagamentoController implements PagamentoAction {

    private static final Logger logger =
            LogManager.getLogger(PagamentoController.class);

    private final PagamentoViewContract view;
    private final Cliente cliente;
    private final Abbonamento abbonamento;

    public PagamentoController(PagamentoViewContract view,
                               Cliente cliente,
                               Abbonamento abbonamento) {
        this.view = view;
        this.cliente = cliente;
        this.abbonamento = abbonamento;

        this.view.setAction(this);
    }

    @Override
    public void handlePaga(String metodo) {
        try {
            // 1) crea pagamento in memoria
            Pagamento pagamento = new Pagamento(metodo, abbonamento.getPrezzo(), new Date());
            pagamento.pagamentoEffettuato();

            // 2) collega abbonamento + pagamento al cliente (in memoria)
            cliente.sottoscriviAbbonamento(abbonamento, pagamento);

            // 3) persistenza su DB: ABBONAMENTO + PAGAMENTO
            if (cliente.getIdCliente() <= 0) {
                logger.warn("ID cliente non valido ({}): impossibile salvare su DB", cliente.getIdCliente());
            } else {
                AbbonamentoDAO.salvaAbbonamento(abbonamento, cliente.getIdCliente());

                PagamentoDAO.salvaPagamento(
                        pagamento,
                        cliente.getIdCliente(),
                        abbonamento.getIdAbbonamento()
                );
            }

            logger.info("Pagamento completato per utente {}. Abbonamento {}",
                    cliente.getUsername(), abbonamento.getTipo());

            view.mostraMessaggioInfo("Pagamento completato con successo!\nAbbonamento attivato.");
            view.dispose();

            HomeView hView = new HomeView(cliente);
            new HomeController(hView, cliente);
            hView.setVisible(true);

        } catch (Exception e) {
            logger.error("Errore durante il pagamento", e);
            view.mostraMessaggioErrore("Errore di sistema durante il pagamento. Riprova più tardi.");
        }
    }

    @Override
    public void handleAnnulla() {
        logger.info("Pagamento annullato dall'utente {}", cliente.getUsername());
        view.mostraMessaggioInfo("Pagamento annullato. Nessun abbonamento attivato.");
        view.dispose();
    }
}
