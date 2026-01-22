package controller;

import model.Abbonamento;
import model.Cliente;
import model.Pagamento;
import view.HomeView;
import view.PagamentoView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import db.dao.AbbonamentoDAO;
import db.dao.PagamentoDAO;

import java.util.Date;

public class PagamentoController {

    private static final Logger logger =
            LogManager.getLogger(PagamentoController.class);

    private final PagamentoView view;
    private final Cliente cliente;
    private final Abbonamento abbonamento;

    public PagamentoController(PagamentoView view,
                               Cliente cliente,
                               Abbonamento abbonamento) {
        this.view = view;
        this.cliente = cliente;
        this.abbonamento = abbonamento;
        this.view.setController(this);
    }

    public void handlePaga(String metodo) {
        // 1) crea pagamento in memoria
        Pagamento pagamento = new Pagamento(metodo,
                abbonamento.getPrezzo(), new Date());
        pagamento.pagamentoEffettuato();

        // 2) collega abbonamento + pagamento al cliente (in memoria)
        cliente.sottoscriviAbbonamento(abbonamento, pagamento);

        // 3) persistenza su DB: ABBONAMENTO + PAGAMENTO
        if (cliente.getIdCliente() <= 0) {
            logger.warn("ID cliente non valido ({}): impossibile salvare su DB", cliente.getIdCliente());
        } else {
            // salva abbonamento (se necessario genera ID_ABBONAMENTO)
            AbbonamentoDAO.salvaAbbonamento(abbonamento, cliente.getIdCliente());

            // salva pagamento legato a quell'abbonamento
            PagamentoDAO.salvaPagamento(pagamento,
                    cliente.getIdCliente(),
                    abbonamento.getIdAbbonamento());
        }

        logger.info("Pagamento completato per utente {}. Abbonamento {}",
                cliente.getUsername(), abbonamento.getTipo());

        view.mostraMessaggioInfo("Pagamento completato con successo!\n" +
                "Abbonamento attivato.");

        view.dispose();

        // 4) apri Home
        HomeView hView = new HomeView(cliente);
        HomeController hController = new HomeController(hView, cliente);
        hView.setVisible(true);
    }

    public void handleAnnulla() {
        logger.info("Pagamento annullato dall'utente {}", cliente.getUsername());
        view.mostraMessaggioInfo("Pagamento annullato. Nessun abbonamento attivato.");
        view.dispose();
    }
}
