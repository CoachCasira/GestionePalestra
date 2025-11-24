package controller;

import db.dao.AbbonamentoDAO;
import db.dao.CorsoDAO;
import db.dao.ConsulenzaDAO;
import model.Abbonamento;
import model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.*;

public class HomeController {

    private static final Logger logger =
            LogManager.getLogger(HomeController.class);

    private final HomeView view;
    private final Cliente cliente;

    public HomeController(HomeView view, Cliente cliente) {
        this.view = view;
        this.cliente = cliente;
        this.view.setController(this);
    }

    /** Vedi abbonamento attivo */
    public void handleVediAbbonamento() {
        Abbonamento abb = cliente.getAbbonamento();
        if (abb == null) {
            view.mostraMessaggioInfo(
                    "Non hai ancora un abbonamento attivo.\n" +
                    "Vai nella sezione 'Acquista abbonamento' per sottoscriverne uno.");
            return;
        }
        view.mostraDettaglioAbbonamento(abb);
    }

    /** Apertura schermata prenotazione consulenza (chiude la Home) */
    public void handlePrenotaConsulenza() {
        view.dispose();

        PrenotaConsulenzaView v = new PrenotaConsulenzaView(cliente);
        new PrenotaConsulenzaController(v, cliente);
        v.setVisible(true);
    }

    /** Apertura schermata prenotazione corso (solo abbonamento CORSI). */
    public void handlePrenotaCorso() {
        // controllo prima che esistano corsi nel DB
        try {
            if (!CorsoDAO.esistonoCorsi()) {
                view.mostraMessaggioErrore(
                        "Al momento non sono presenti corsi a catalogo.\n" +
                        "Contatta la palestra per maggiori informazioni.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.mostraMessaggioErrore(
                    "Si è verificato un errore nel caricamento dei corsi.\n" +
                    "Riprova più tardi.");
            return;
        }

        // se ci sono corsi → apro la schermata e chiudo la Home
        view.dispose();

        PrenotaCorsoView v = new PrenotaCorsoView(cliente);
        new PrenotaCorsoController(v, cliente);
        v.setVisible(true);
    }

    /** Vedi corsi prenotati */
    public void handleVediCorsi() {
        try {
            String testo = CorsoDAO.buildDettaglioIscrizioniPerCliente(cliente.getIdCliente());
            view.mostraDettaglioCorsi(testo);
        } catch (Exception e) {
            e.printStackTrace();
            view.mostraMessaggioErrore("Errore nel caricamento dei corsi prenotati.");
        }
    }

    /** Vedi consulenze prenotate */
    public void handleVediConsulenza() {
        try {
            String dettaglio = ConsulenzaDAO.buildDettaglioConsulenzePerCliente(cliente.getIdCliente());
            view.mostraDettaglioConsulenza(dettaglio);
        } catch (Exception e) {
            e.printStackTrace();
            view.mostraMessaggioErrore("Errore nel caricamento delle consulenze.");
        }
    }

    /** Disdetta abbonamento (bloccata se ci sono consulenze o corsi futuri) */
    public void handleDisdiciAbbonamento() {
        Abbonamento abb = cliente.getAbbonamento();
        if (abb == null) {
            view.mostraMessaggioInfo("Non hai alcun abbonamento attivo da disdire.");
            return;
        }

        try {
            if (ConsulenzaDAO.esistonoConsulenzeFuturePerCliente(cliente.getIdCliente())) {
                view.mostraMessaggioErrore(
                        "Non puoi disdire l'abbonamento perché hai consulenze future prenotate.");
                return;
            }

            if (CorsoDAO.esistonoIscrizioniFuturePerCliente(cliente.getIdCliente())) {
                view.mostraMessaggioErrore(
                        "Non puoi disdire l'abbonamento perché hai corsi futuri prenotati.");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            view.mostraMessaggioErrore(
                    "Errore nel controllo di consulenze/corsi futuri.\n" +
                    "Riprova più tardi.");
            return;
        }

        boolean conferma = ThemedDialog.showConfirm(
                view,
                "Conferma disdetta",
                "Sei sicuro di voler disdire l’abbonamento attivo?"
        );

        if (!conferma) return;

        try {
            AbbonamentoDAO.disdiciAbbonamentoPerUsername(cliente.getUsername());
            cliente.setAbbonamento(null);

            view.mostraMessaggioInfo("Abbonamento disdetto con successo.");
            logger.info("Abbonamento disdetto per utente {}", cliente.getUsername());

            SelezionaAbbonamentoView sView = new SelezionaAbbonamentoView(cliente);
            new SelezionaAbbonamentoController(sView, cliente);
            sView.setVisible(true);
            view.dispose();

        } catch (Exception e) {
            logger.error("Errore durante la disdetta per utente {}", cliente.getUsername(), e);
            view.mostraMessaggioErrore(
                    "Si è verificato un errore durante la disdetta dell’abbonamento.");
        }
    }

    /** Logout utente */
    public void handleLogout() {
        logger.info("Logout utente {}", cliente.getUsername());
        view.dispose();

        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }

    /** Apertura dialog disdetta consulenza */
    public void handleApriDisdettaConsulenza() {
        DisdiciConsulenzaDialog dialog = new DisdiciConsulenzaDialog(view, cliente);
        dialog.setVisible(true);
    }
}
