package controller;

import action.HomeAction;

import db.dao.AbbonamentoDAO;
import db.dao.ConsulenzaDAO;
import db.dao.corso.CorsoDAO;
import db.dao.corso.IscrizioneDAO;

import model.Abbonamento;
import model.Cliente;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import service.PanoramicaPalestraService;
import service.PanoramicaPalestraServiceIf;

import view.*;
import view.dialog.*;

public class HomeController implements HomeAction {

    private static final Logger logger =
            LogManager.getLogger(HomeController.class);

    private final HomeView view;
    private final Cliente cliente;
    private final PanoramicaPalestraServiceIf panoramicaService;

    public HomeController(HomeView view, Cliente cliente) {
        this.view = view;
        this.cliente = cliente;
        this.panoramicaService = new PanoramicaPalestraService();

        // registro le azioni: la view non conosce più il controller concreto
        this.view.setAction(this);
    }

    // ====== implementazione HomeAction (delegate ai vecchi metodi handle) ======

    @Override public void onPrenotaConsulenza() { handlePrenotaConsulenza(); }
    @Override public void onPrenotaCorso() { handlePrenotaCorso(); }
    @Override public void onVediAbbonamento() { handleVediAbbonamento(); }
    @Override public void onVediCorsi() { handleVediCorsi(); }
    @Override public void onVediConsulenza() { handleVediConsulenza(); }
    @Override public void onDisdiciAbbonamento() { handleDisdiciAbbonamento(); }
    @Override public void onLogout() { handleLogout(); }
    @Override public void onApriDisdettaConsulenza() { handleApriDisdettaConsulenza(); }
    @Override public void onApriDisdettaCorso() { handleApriDisdettaCorso(); }
    @Override public void onPanoramicaPalestra() { handlePanoramicaPalestra(); }

    /** Apertura schermata prenotazione consulenza (chiude la Home) */
    public void handlePrenotaConsulenza() {
        view.dispose();

        PrenotaConsulenzaView v = new PrenotaConsulenzaView(cliente);
        new PrenotaConsulenzaController(v, cliente);
        v.setVisible(true);
    }

    /** Apertura schermata prenotazione corso (solo abbonamento CORSI). */
    public void handlePrenotaCorso() {
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

        view.dispose();

        PrenotaCorsoView v = new PrenotaCorsoView(cliente);
        new PrenotaCorsoController(v, cliente);
        v.setVisible(true);
    }

    /** Vedi dettaglio abbonamento. */
    public void handleVediAbbonamento() {
        Abbonamento abb = cliente.getAbbonamento();
        if (abb == null) {
            view.mostraMessaggioInfo(
                    "Non hai ancora un abbonamento attivo.\n" +
                    "Vai nella sezione 'Acquista abbonamento' per sottoscriverne uno.");
            return;
        }

        view.setVisible(false);
        view.mostraDettaglioAbbonamento(abb);
        view.setVisible(true);
    }

    /** Vedi corsi prenotati */
    public void handleVediCorsi() {
        try {
            String testo = IscrizioneDAO.buildDettaglioIscrizioniPerCliente(cliente.getIdCliente());
            view.setVisible(false);
            view.mostraDettaglioCorsi(testo);
            view.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            view.mostraMessaggioErrore("Errore nel caricamento dei corsi prenotati.");
        }
    }

    /** Vedi consulenze prenotate */
    public void handleVediConsulenza() {
        try {
            String dettaglio = ConsulenzaDAO.buildDettaglioConsulenzePerCliente(cliente.getIdCliente());
            view.setVisible(false);
            view.mostraDettaglioConsulenza(dettaglio);
            view.setVisible(true);

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

            if (IscrizioneDAO.esistonoIscrizioniFuturePerCliente(cliente.getIdCliente())) {
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
        try {
            if (!ConsulenzaDAO.esistonoConsulenzeFuturePerCliente(cliente.getIdCliente())) {
                view.mostraMessaggioInfo("Non hai consulenze future prenotate da poter disdire.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.mostraMessaggioErrore(
                    "Errore nel caricamento delle consulenze future.\n" +
                    "Riprova più tardi.");
            return;
        }

        DisdiciConsulenzaDialog dialog = new DisdiciConsulenzaDialog(view, cliente);
        dialog.setVisible(true);
    }

    /** Apertura dialog disdetta corso */
    public void handleApriDisdettaCorso() {
        try {
            if (!IscrizioneDAO.esistonoIscrizioniFuturePerCliente(cliente.getIdCliente())) {
                view.mostraMessaggioInfo("Non hai corsi futuri prenotati da poter disdire.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.mostraMessaggioErrore(
                    "Errore nel caricamento dei corsi prenotati.\n" +
                    "Riprova più tardi.");
            return;
        }

        DisdiciCorsoDialog dialog = new DisdiciCorsoDialog(view, cliente);
        dialog.setVisible(true);
    }

    public void handlePanoramicaPalestra() {
        Abbonamento abb = cliente.getAbbonamento();
        if (abb == null) {
            view.mostraMessaggioInfo(
                    "Per visualizzare la panoramica della palestra devi avere\n" +
                    "un abbonamento attivo.");
            return;
        }

        try {
            String testo = panoramicaService.generaPanoramica(cliente);

            if (testo == null || testo.isEmpty()) {
                view.mostraMessaggioInfo(
                        "Per il tuo tipo di abbonamento non è disponibile alcuna area da visualizzare.");
                return;
            }

            view.setVisible(false);
            view.mostraPanoramicaPalestra(testo);
            view.setVisible(true);

        } catch (Exception e) {
            logger.error("Errore nel caricamento panoramica palestra per utente {}",
                    cliente.getUsername(), e);
            view.mostraMessaggioErrore(
                    "Si è verificato un errore nel caricamento della panoramica palestra.\n" +
                    "Riprova più tardi.");
        }
    }
}
