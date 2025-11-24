package controller;

import db.dao.CorsoDAO;
import db.dao.CorsoDAO.CorsoInfo;
import db.dao.CorsoDAO.LezioneInfo;
import model.Cliente;
import view.HomeView;
import view.PrenotaCorsoView;
import view.ThemedDialog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PrenotaCorsoController {

    private final PrenotaCorsoView view;
    private final Cliente cliente;

    private final List<CorsoInfo> corsi = new ArrayList<>();
    private final List<LezioneInfo> lezioniCorsoSelezionato = new ArrayList<>();

    public PrenotaCorsoController(PrenotaCorsoView view, Cliente cliente) {
        this.view = view;
        this.cliente = cliente;
        this.view.setController(this);

        caricaCorsi();
    }

    private void caricaCorsi() {
        try {
            corsi.clear();
            corsi.addAll(CorsoDAO.getTuttiICorsi());

            String[] nomi = corsi.stream()
                    .map(c -> c.nome)
                    .toArray(String[]::new);

            view.setCorsi(nomi);

            if (!corsi.isEmpty()) {
                // selezione di default primo corso
                view.getRootPane().requestFocus();
                view.setDescrizioneCorso(corsi.get(0).descrizione +
                        "\n\nDurata: " + corsi.get(0).durataMinuti + " minuti.");
                handleCorsoSelezionato(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Errore nel caricamento dei corsi dal database.",
                    true);
        }
    }

    /** Chiamato dalla view quando l’utente cambia selezione corso. */
    public void handleCorsoSelezionato(int index) {
        if (index < 0 || index >= corsi.size()) {
            view.setDescrizioneCorso("");
            view.setLezioni(new String[0]);
            lezioniCorsoSelezionato.clear();
            return;
        }

        CorsoInfo corso = corsi.get(index);

        String descrEstesa = corso.descrizione +
                "\n\nDurata: " + corso.durataMinuti + " minuti.";
        view.setDescrizioneCorso(descrEstesa);

        try {
            lezioniCorsoSelezionato.clear();
            lezioniCorsoSelezionato.addAll(
                    CorsoDAO.getLezioniPerCorso(corso.idCorso)
            );

            String[] righe = lezioniCorsoSelezionato.stream()
                    .map(l -> String.format(
                            "%s ore %s - Istruttore: %s - Posti rimanenti: %d",
                            l.data, l.ora, l.nomeIstruttore, l.postiDisponibili()))
                    .toArray(String[]::new);

            view.setLezioni(righe);

        } catch (Exception e) {
            e.printStackTrace();
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Errore nel caricamento delle lezioni per il corso selezionato.",
                    true);
            view.setLezioni(new String[0]);
        }
    }

    /** Conferma iscrizione al corso/lezione selezionata. */
    public void handleConfermaIscrizione() {
        int idxCorso = view.getIndiceCorsoSelezionato();
        int idxLezione = view.getIndiceLezioneSelezionata();

        if (idxCorso < 0 || idxCorso >= corsi.size()) {
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Seleziona prima un corso.",
                    true);
            return;
        }
        if (idxLezione < 0 || idxLezione >= lezioniCorsoSelezionato.size()) {
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Seleziona una data/ora per il corso.",
                    true);
            return;
        }

        CorsoInfo corso = corsi.get(idxCorso);
        LezioneInfo lezione = lezioniCorsoSelezionato.get(idxLezione);

        // controlli
        try {
            // posti disponibili?
            if (!CorsoDAO.haPostiDisponibili(lezione.idLezione)) {
                ThemedDialog.showMessage(view,
                        "Posti esauriti",
                        "Per questa data del corso i posti sono terminati.\n" +
                        "Seleziona un altro orario.",
                        true);
                // ricarico lezioni (per aggiornare eventuali numeri)
                handleCorsoSelezionato(idxCorso);
                return;
            }

            // conflitto per cliente (altri corsi nello stesso intervallo)
            LocalDate data = lezione.data;
            LocalTime ora  = lezione.ora;
            int durata     = lezione.durataMinuti;

            if (CorsoDAO.esisteConflittoPerCliente(
                    cliente.getIdCliente(), data, ora, durata)) {

                ThemedDialog.showMessage(view,
                        "Conflitto orario",
                        "Hai già un altro corso prenotato in questo intervallo di tempo.\n" +
                        "Scegli un orario differente.",
                        true);
                return;
            }

            // iscrizione effettiva
            CorsoDAO.iscriviClienteALezione(cliente.getIdCliente(), lezione.idLezione);

            String msg = String.format(
                    "Iscrizione completata.\n\nCorso: %s\nData: %s ore %s\nIstruttore: %s\nDurata: %d minuti",
                    corso.nome, lezione.data, lezione.ora,
                    lezione.nomeIstruttore, lezione.durataMinuti
            );

            ThemedDialog.showMessage(view, "Info", msg, false);

            // torno alla Home
            view.dispose();
            HomeView home = new HomeView(cliente);
            new HomeController(home, cliente);
            home.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Si è verificato un errore durante l'iscrizione al corso.",
                    true);
        }
    }

    public void handleAnnulla() {
        view.dispose();
        HomeView home = new HomeView(cliente);
        new HomeController(home, cliente);
        home.setVisible(true);
    }
}
