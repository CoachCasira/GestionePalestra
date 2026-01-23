package controller;

import action.PrenotaCorsoAction;
import action.PrenotaCorsoViewContract;
import model.Cliente;
import model.corsi.CorsoInfo;
import model.corsi.LezioneInfo;
import view.HomeView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import db.dao.corso.CorsoDAO;
import db.dao.corso.IscrizioneDAO;
import db.dao.corso.LezioneDAO;

public class PrenotaCorsoController implements PrenotaCorsoAction {

    private final PrenotaCorsoViewContract view;
    private final Cliente cliente;

    private final List<CorsoInfo> corsi = new ArrayList<>();
    private final List<LezioneInfo> lezioniCorsoSelezionato = new ArrayList<>();

    public PrenotaCorsoController(PrenotaCorsoViewContract view, Cliente cliente) {
        this.view = view;
        this.cliente = cliente;

        // la view conosce solo l’interfaccia
        this.view.setAction(this);

        caricaCorsi();
    }

    private void caricaCorsi() {
        try {
            LezioneDAO.aggiornaDateLezioniAllaSettimanaCorrente();
        } catch (Exception e) {
            // non blocco tutto: al massimo i dati saranno meno “puliti”
            e.printStackTrace();
        }

        try {
            corsi.clear();
            corsi.addAll(CorsoDAO.getTuttiICorsi());

            String[] nomi = corsi.stream()
                    .map(c -> c.nome)
                    .toArray(String[]::new);

            view.setCorsi(nomi);

            if (!corsi.isEmpty()) {
                view.setDescrizioneCorso(corsi.get(0).descrizione +
                        "\n\nDurata: " + corsi.get(0).durataMinuti + " minuti.");
                onCorsoSelezionato(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            view.mostraErrore("Errore", "Errore nel caricamento dei corsi dal database.");
        }
    }

    @Override
    public void onCorsoSelezionato(int index) {
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
                    LezioneDAO.getLezioniPerCorso(corso.idCorso)
            );

            String[] righe = lezioniCorsoSelezionato.stream()
                    .map(l -> String.format(
                            "%s ore %s - Istruttore: %s - Posti rimanenti: %d",
                            l.data, l.ora, l.nomeIstruttore, l.postiDisponibili()))
                    .toArray(String[]::new);

            view.setLezioni(righe);

        } catch (Exception e) {
            e.printStackTrace();
            view.mostraErrore("Errore",
                    "Errore nel caricamento delle lezioni per il corso selezionato.");
            view.setLezioni(new String[0]);
        }
    }

    @Override
    public void onConfermaIscrizione() {
        int idxCorso = view.getIndiceCorsoSelezionato();
        int idxLezione = view.getIndiceLezioneSelezionata();

        if (idxCorso < 0 || idxCorso >= corsi.size()) {
            view.mostraErrore("Errore", "Seleziona prima un corso.");
            return;
        }
        if (idxLezione < 0 || idxLezione >= lezioniCorsoSelezionato.size()) {
            view.mostraErrore("Errore", "Seleziona una data/ora per il corso.");
            return;
        }

        CorsoInfo corso = corsi.get(idxCorso);
        LezioneInfo lezione = lezioniCorsoSelezionato.get(idxLezione);

        try {
            if (!IscrizioneDAO.haPostiDisponibili(lezione.idLezione)) {
                view.mostraErrore("Posti esauriti",
                        "Per questa data del corso i posti sono terminati.\nSeleziona un altro orario.");
                onCorsoSelezionato(idxCorso);
                return;
            }

            LocalDate data = lezione.data;
            LocalTime ora  = lezione.ora;
            int durata     = lezione.durataMinuti;

            if (IscrizioneDAO.esisteConflittoPerCliente(
                    cliente.getIdCliente(), data, ora, durata)) {

                view.mostraErrore("Conflitto orario",
                        "Hai già un altro corso prenotato in questo intervallo di tempo.\nScegli un orario differente.");
                return;
            }

            IscrizioneDAO.iscriviClienteALezione(cliente.getIdCliente(), lezione.idLezione);

            String msg = String.format(
                    "Iscrizione completata.\n\nCorso: %s\nData: %s ore %s\nIstruttore: %s\nDurata: %d minuti",
                    corso.nome, lezione.data, lezione.ora,
                    lezione.nomeIstruttore, lezione.durataMinuti
            );

            view.mostraInfo("Info", msg);

            view.close();
            HomeView home = new HomeView(cliente);
            new HomeController(home, cliente);
            home.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            view.mostraErrore("Errore",
                    "Si è verificato un errore durante l'iscrizione al corso.");
        }
    }

    @Override
    public void onAnnulla() {
        view.close();
        HomeView home = new HomeView(cliente);
        new HomeController(home, cliente);
        home.setVisible(true);
    }
}
