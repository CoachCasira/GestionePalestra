package controller;

import action.PrenotaConsulenzaAction;
import action.PrenotaConsulenzaViewContract;
import db.dao.ConsulenzaDAO;
import db.dao.DipendenteDAO;
import db.dao.DipendenteDAO.DipendenteInfo;
import model.Cliente;
import model.Consulenza;
import view.HomeView;
import view.PrenotaConsulenzaView;
import view.dialog.ThemedDialog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrenotaConsulenzaController implements PrenotaConsulenzaAction {

    private final PrenotaConsulenzaViewContract view;
    private final Cliente cliente;

    private final Map<String, Integer> mappaDipendenti = new HashMap<>();

    public PrenotaConsulenzaController(PrenotaConsulenzaViewContract view, Cliente cliente) {
        this.view = view;
        this.cliente = cliente;

        // collega l'action alla view (senza dipendenza view->controller)
        if (view instanceof PrenotaConsulenzaView) {
            ((PrenotaConsulenzaView) view).setController(this);
        }

        handleTipoSelezionato(view.getTipoSelezionato());
    }

    @Override
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

            default:
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
            view.setDescrizioneDipendente("");

        } catch (Exception e) {
            e.printStackTrace();
            ThemedDialog.showMessage(view.asWindow(),
                    "Errore",
                    "Errore nel caricamento dei dipendenti dal database.",
                    true);
        }
    }

    @Override
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

    @Override
    public void handleConfermaPrenotazione() {
        String tipo    = view.getTipoSelezionato();
        String dataStr = view.getDataText();
        String oraStr  = view.getOraText();
        String nomeDip = view.getDipendenteSelezionato();
        String note    = view.getNote();

        if (dataStr.isEmpty() || oraStr.isEmpty() || nomeDip == null) {
            ThemedDialog.showMessage(view.asWindow(),
                    "Errore",
                    "Compila data, ora e seleziona un dipendente.",
                    true);
            return;
        }

        LocalDate data;
        LocalTime ora;
        try {
            data = LocalDate.parse(dataStr);
            ora  = LocalTime.parse(oraStr);
        } catch (DateTimeParseException e) {
            ThemedDialog.showMessage(view.asWindow(),
                    "Errore",
                    "Formato data/ora non valido. Usa yyyy-MM-dd e HH:mm.",
                    true);
            return;
        }

        Integer idDip = mappaDipendenti.get(nomeDip);
        if (idDip == null) {
            ThemedDialog.showMessage(view.asWindow(),
                    "Errore",
                    "Dipendente selezionato non valido.",
                    true);
            return;
        }

        try {
            if (ConsulenzaDAO.esisteConflitto(
                    cliente.getIdCliente(), idDip, tipo, data, ora)) {

                ThemedDialog.showMessage(view.asWindow(),
                        "Errore",
                        "Esiste già una consulenza nello stesso intervallo orario\n" +
                        "per il cliente o per il professionista selezionato.\n" +
                        "Modifica l'orario o il giorno della nuova prenotazione.",
                        true);
                return;
            }

            Consulenza nuova = new Consulenza(
                    cliente.getIdCliente(),
                    idDip,
                    tipo,
                    data,
                    ora,
                    note
            );
            ConsulenzaDAO.inserisci(nuova);

            ThemedDialog.showMessage(view.asWindow(),
                    "Info",
                    "Consulenza prenotata con successo.\n\n" + nuova,
                    false);

            view.dispose();
            HomeView home = new HomeView(cliente);
            new HomeController(home, cliente);
            home.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            ThemedDialog.showMessage(view.asWindow(),
                    "Errore",
                    "Si è verificato un errore durante il salvataggio della consulenza.",
                    true);
        }
    }

    @Override
    public void handleAnnulla() {
        view.dispose();

        HomeView home = new HomeView(cliente);
        new HomeController(home, cliente);
        home.setVisible(true);
    }
}
