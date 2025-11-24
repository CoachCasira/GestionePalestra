package controller;

import db.dao.ConsulenzaDAO;
import db.dao.DipendenteDAO;
import db.dao.DipendenteDAO.DipendenteInfo;
import model.Cliente;
import model.Consulenza;
import view.HomeView;
import view.PrenotaConsulenzaView;
import view.ThemedDialog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrenotaConsulenzaController {

    private final PrenotaConsulenzaView view;
    private final Cliente cliente;

    // mappa nome completo -> id dipendente, per la combo
    private Map<String, Integer> mappaDipendenti = new HashMap<>();

    public PrenotaConsulenzaController(PrenotaConsulenzaView view, Cliente cliente) {
        this.view = view;
        this.cliente = cliente;
        this.view.setController(this);

        // inizializza tipo default
        handleTipoSelezionato(view.getTipoSelezionato());
    }

    public void handleTipoSelezionato(String tipo) {
        String descr;
        String ruoloDb;

        switch (tipo) {
            case "NUTRIZIONISTA":
                descr =
                        "Consulenza nutrizionale con un nostro nutrizionista.\n" +
                        "Analisi delle abitudini alimentari, definizione di un piano\n" +
                        "personalizzato e chiarimento dei tuoi obiettivi.\n\n" +
                        "Durata indicativa: 60 minuti.";
                ruoloDb = "NUTRIZIONISTA";
                break;

            case "ISTRUTTORE_CORSO":
                descr =
                        "Consulenza con un istruttore dei corsi di gruppo.\n" +
                        "Puoi discutere organizzazione dei corsi, livello di difficoltà\n" +
                        "e suggerimenti per il corso più adatto a te.\n\n" +
                        "Durata indicativa: 45 minuti.";
                ruoloDb = "ISTRUTTORE_CORSO";
                break;

            default: // PERSONAL_TRAINER
                descr =
                        "Consulenza con Personal Trainer.\n" +
                        "Analisi obiettivi, valutazione iniziale e definizione\n" +
                        "di un piano di allenamento personalizzato.\n\n" +
                        "Durata indicativa: 30 minuti.";
                ruoloDb = "PERSONAL_TRAINER";
                break;
        }

        view.setDescrizioneTipo(descr);

        try {
            List<DipendenteInfo> lista = DipendenteDAO.findByRuolo(ruoloDb);
            mappaDipendenti.clear();
            String[] nomi = new String[lista.size()];
            for (int i = 0; i < lista.size(); i++) {
                DipendenteInfo info = lista.get(i);
                nomi[i] = info.nomeCompleto;
                mappaDipendenti.put(info.nomeCompleto, info.id);
            }
            view.setDipendenti(nomi);

            // reset descrizione dipendente quando cambio tipo
            view.setDescrizioneDipendente("");

        } catch (Exception e) {
            e.printStackTrace();
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Errore nel caricamento dei dipendenti dal database.",
                    true);
        }
    }

    /** Chiamato dalla view quando l’utente cambia il dipendente nella combo */
    public void handleDipendenteSelezionato(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isEmpty()) {
            view.setDescrizioneDipendente("");
            return;
        }

        Integer idDip = mappaDipendenti.get(nomeCompleto);
        if (idDip == null) {
            view.setDescrizioneDipendente("");
            return;
        }

        try {
            String descr = DipendenteDAO.getDescrizioneDipendente(idDip);
            view.setDescrizioneDipendente(descr);
        } catch (Exception e) {
            e.printStackTrace();
            view.setDescrizioneDipendente(
                    "Impossibile caricare i dettagli del dipendente selezionato.");
        }
    }

    public void handleConfermaPrenotazione() {
        String tipo    = view.getTipoSelezionato();   // PERSONAL_TRAINER / NUTRIZIONISTA / ISTRUTTORE_CORSO
        String dataStr = view.getDataText();
        String oraStr  = view.getOraText();
        String nomeDip = view.getDipendenteSelezionato();
        String note    = view.getNote();

        if (dataStr.isEmpty() || oraStr.isEmpty() || nomeDip == null) {
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Compila data, ora e seleziona un dipendente.",
                    true);
            return;
        }

        LocalDate data;
        LocalTime ora;
        try {
            data = LocalDate.parse(dataStr);  // formato yyyy-MM-dd
            ora  = LocalTime.parse(oraStr);   // formato HH:mm
        } catch (DateTimeParseException e) {
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Formato data/ora non valido. Usa yyyy-MM-dd e HH:mm.",
                    true);
            return;
        }

        Integer idDip = mappaDipendenti.get(nomeDip);
        if (idDip == null) {
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Dipendente selezionato non valido.",
                    true);
            return;
        }

        try {
            // controllo conflitto nel DB, considerando:
            // - altre consulenze dello stesso cliente
            // - altre consulenze dello stesso dipendente
            if (ConsulenzaDAO.esisteConflitto(
                    cliente.getIdCliente(), idDip, tipo, data, ora)) {

                ThemedDialog.showMessage(view,
                        "Errore",
                        "Esiste già una consulenza nello stesso intervallo orario\n" +
                        "per il cliente o per il professionista selezionato.\n" +
                        "Modifica l'orario o il giorno della nuova prenotazione.",
                        true);
                return;
            }

            // nessun conflitto → inserisco
            Consulenza nuova = new Consulenza(
                    cliente.getIdCliente(),
                    idDip,
                    tipo,
                    data,
                    ora,
                    note
            );
            ConsulenzaDAO.inserisci(nuova);

            ThemedDialog.showMessage(view,
                    "Info",
                    "Consulenza prenotata con successo.\n\n" + nuova,
                    false);

            // Torno all'area personale
            view.dispose();
            HomeView home = new HomeView(cliente);
            new HomeController(home, cliente);
            home.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            ThemedDialog.showMessage(view,
                    "Errore",
                    "Si è verificato un errore durante il salvataggio della consulenza.",
                    true);
        }
    }

    public void handleAnnulla() {
        // Torna semplicemente all’home senza salvare nulla
        view.dispose();
        HomeView home = new HomeView(cliente);
        new HomeController(home, cliente);
        home.setVisible(true);
    }
}
