package view.dialog;

import db.dao.CorsoDAO;
import db.dao.CorsoDAO.IscrizioneInfo;
import model.Cliente;
import view.HomeView;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dialog per disdire un corso futuro prenotato dal cliente.
 * Dimensioni e tema coerenti con il resto dell'app (420x650, dark + arancione),
 * ma con la stessa logica funzionante basata su CorsoDAO.getIscrizioniFuturePerCliente(...)
 */
public class DisdiciCorsoDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // stessa palette della Home
    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    private final HomeView parent;
    private final Cliente  cliente;

    private List<IscrizioneInfo> iscrizioniFuture;

    private JList<String>        lista;
    private DefaultListModel<String> listModel;

    public DisdiciCorsoDialog(HomeView parent, Cliente cliente) {
        super(parent, "Disdici corso", true);
        this.parent  = parent;
        this.cliente = cliente;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(420, 650);              // stessa “taglia telefono” di Home/Login
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        caricaIscrizioni();
    }

    // ==========================================================
    //  UI
    // ==========================================================
    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        setContentPane(main);

        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Seleziona un corso da cui disiscriverti");
        lblTitle.setForeground(ORANGE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(
                "Puoi disdire solo i corsi che iniziano tra almeno 30 minuti.");
        lblSub.setForeground(TEXT_GRAY);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        main.add(header, BorderLayout.NORTH);

        // ===== LISTA CORSI FUTURI =====
        listModel = new DefaultListModel<>();
        lista     = new JList<>(listModel);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setBackground(CARD_BG);
        lista.setForeground(Color.WHITE);
        lista.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lista.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scroll = new JScrollPane(
                lista,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(DARK_BG);
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        center.add(scroll, BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);

        // ===== FOOTER BOTTONI =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        footer.setBackground(DARK_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JButton btnDisdici = creaBottoneArancione("Disiscriviti dal corso selezionato");
        JButton btnChiudi  = creaBottoneSoloBordo("Annulla");

        // larghezze simili agli altri dialog
        btnDisdici.setPreferredSize(new Dimension(230, 40));
        btnChiudi.setPreferredSize(new Dimension(120, 40));

        btnDisdici.addActionListener(e -> handleDisdici());
        btnChiudi.addActionListener(e -> dispose());

        footer.add(btnDisdici);
        footer.add(btnChiudi);

        main.add(footer, BorderLayout.SOUTH);
    }

    // ==========================================================
    //  CARICAMENTO ISCRIZIONI FUTURE (LOGICA ORIGINALE)
    // ==========================================================
    private void caricaIscrizioni() {
        try {
            iscrizioniFuture = CorsoDAO.getIscrizioniFuturePerCliente(cliente.getIdCliente());
            listModel.clear();

            if (iscrizioniFuture.isEmpty()) {
                view.dialog.ThemedDialog.showMessage(
                        parent,
                        "Info",
                        "Non hai corsi futuri da cui disiscriverti.",
                        false
                );
                dispose();
                return;
            }

            for (IscrizioneInfo i : iscrizioniFuture) {
                String s = String.format(
                        "%s ore %s – %s (Istruttore: %s)",
                        i.data, i.ora,
                        i.nomeCorso,
                        i.nomeIstruttore
                );
                listModel.addElement(s);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            view.dialog.ThemedDialog.showMessage(
                    parent,
                    "Errore",
                    "Errore nel caricamento dei corsi futuri.",
                    true
            );
            dispose();
        }
    }

    // ==========================================================
    //  HANDLER DISDETTA (LOGICA ORIGINALE + TEMA NUOVO)
    // ==========================================================
    private void handleDisdici() {
        if (iscrizioniFuture == null || iscrizioniFuture.isEmpty()) {
            dispose();
            return;
        }

        int idx = lista.getSelectedIndex();
        if (idx < 0) {
            view.dialog.ThemedDialog.showMessage(
                    this,
                    "Errore",
                    "Seleziona prima un corso da cui disiscriverti.",
                    true
            );
            return;
        }

        IscrizioneInfo sel = iscrizioniFuture.get(idx);

        // controllo 30 minuti prima dell'inizio
        LocalDateTime now    = LocalDateTime.now();
        LocalDateTime inizio = sel.getInizio();
        long minutiMancanti  = Duration.between(now, inizio).toMinutes();

        if (minutiMancanti < 30) {
            view.dialog.ThemedDialog.showMessage(
                    this,
                    "Impossibile disdire",
                    "Il corso selezionato inizia tra meno di 30 minuti oppure è già iniziato.\n" +
                    "Non è più possibile disiscriversi.",
                    true
            );
            return;
        }

        boolean conferma = view.dialog.ThemedDialog.showConfirm(
                this,
                "Conferma disdetta corso",
                "Vuoi davvero disiscriverti dal corso \"" + sel.nomeCorso +
                        "\" del " + sel.data + " alle " + sel.ora + "?"
        );

        if (!conferma) {
            return;
        }

        try {
            CorsoDAO.disiscriviClienteDaLezione(cliente.getIdCliente(), sel.idLezione);
            view.dialog.ThemedDialog.showMessage(
                    this,
                    "Info",
                    "Ti sei disiscritto dal corso selezionato.",
                    false
            );
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            view.dialog.ThemedDialog.showMessage(
                    this,
                    "Errore",
                    "Si è verificato un errore durante la disiscrizione dal corso.",
                    true
            );
        }
    }

    // ==========================================================
    //  Bottoni in stile template
    // ==========================================================
    private JButton creaBottoneArancione(String testo) {
        JButton b = new JButton(testo);
        b.setBackground(ORANGE);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b.isEnabled()) b.setBackground(ORANGE_HO);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(ORANGE);
            }
        });
        return b;
    }

    private JButton creaBottoneSoloBordo(String testo) {
        JButton b = new JButton(testo);
        b.setBackground(DARK_BG);
        b.setForeground(ORANGE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(ORANGE));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b.isEnabled()) b.setBackground(new Color(40, 40, 40));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(DARK_BG);
            }
        });
        return b;
    }
}
