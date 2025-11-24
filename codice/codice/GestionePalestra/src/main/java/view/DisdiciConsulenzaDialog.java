package view;

import db.dao.ConsulenzaDAO;
import db.dao.ConsulenzaDAO.ConsulenzaInfo;
import model.Cliente;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DisdiciConsulenzaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    private final Cliente cliente;
    private final JFrame parent;
    private List<ConsulenzaInfo> consulenzeFuture;

    private JList<String> lista;
    private DefaultListModel<String> listModel;

    public DisdiciConsulenzaDialog(JFrame parent, Cliente cliente) {
        super(parent, "Disdici consulenza", true);
        this.parent = parent;
        this.cliente = cliente;

        initUI();
        caricaConsulenze();
    }

    private void initUI() {
        setSize(600, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        setContentPane(main);

        // header
        JPanel header = new JPanel();
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Seleziona una consulenza da disdire");
        lblTitle.setForeground(ORANGE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Puoi disdire solo le consulenze con inizio tra almeno 24 ore.");
        lblSub.setForeground(TEXT_GRAY);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(5));
        header.add(lblSub);

        main.add(header, BorderLayout.NORTH);

        // lista
        listModel = new DefaultListModel<>();
        lista = new JList<>(listModel);
        lista.setBackground(CARD_BG);
        lista.setForeground(Color.WHITE);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(DARK_BG);
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        center.add(scroll, BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);

        // footer bottoni
        JPanel footer = new JPanel();
        footer.setBackground(DARK_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JButton btnDisdici = creaBottoneArancione("Disdici selezionata");
        JButton btnChiudi  = creaBottoneSoloBordo("Annulla");

        btnDisdici.addActionListener(e -> handleDisdici());
        btnChiudi.addActionListener(e -> dispose());

        footer.add(btnDisdici);
        footer.add(Box.createHorizontalStrut(15));
        footer.add(btnChiudi);

        main.add(footer, BorderLayout.SOUTH);
    }

    private void caricaConsulenze() {
        try {
            consulenzeFuture = ConsulenzaDAO.getConsulenzeFuturePerCliente(cliente.getIdCliente());
            listModel.clear();

            // per sicurezza: se non ce ne sono, chiudo il dialog in silenzio
            if (consulenzeFuture.isEmpty()) {
                dispose();
                return;
            }

            for (ConsulenzaInfo c : consulenzeFuture) {
                String s = String.format(
                        "%s %s - %s (%s) - %s",
                        c.data,
                        c.ora,
                        c.nomeDip,
                        c.ruoloDip,
                        c.tipo
                );
                listModel.addElement(s);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ThemedDialog.showMessage(parent,
                    "Errore",
                    "Errore nel caricamento delle consulenze future.",
                    true);
            dispose();
        }
    }

    private void handleDisdici() {
        if (consulenzeFuture == null || consulenzeFuture.isEmpty()) {
            dispose();
            return;
        }

        int idx = lista.getSelectedIndex();
        if (idx < 0) {
            ThemedDialog.showMessage(this,
                    "Errore",
                    "Seleziona prima una consulenza da disdire.",
                    true);
            return;
        }

        ConsulenzaInfo sel = consulenzeFuture.get(idx);

        // controllo 24 ore
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dataOra = sel.getDataOra();
        long oreMancanti = Duration.between(now, dataOra).toHours();

        if (oreMancanti < 24) {
            ThemedDialog.showMessage(this,
                    "Impossibile disdire",
                    "La consulenza selezionata inizia tra meno di 24 ore.\n" +
                    "Non è più possibile disdirla.",
                    true);
            return;
        }

        boolean conferma = ThemedDialog.showConfirm(
                this,
                "Conferma disdetta",
                "Vuoi davvero disdire la consulenza del " +
                        sel.data + " alle " + sel.ora + "?"
        );

        if (!conferma) {
            return;
        }

        try {
            ConsulenzaDAO.disdiciConsulenza(sel.id);
            ThemedDialog.showMessage(this,
                    "Info",
                    "Consulenza disdetta con successo.",
                    false);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            ThemedDialog.showMessage(this,
                    "Errore",
                    "Si è verificato un errore durante la disdetta della consulenza.",
                    true);
        }
    }

    private JButton creaBottoneArancione(String testo) {
        JButton b = new JButton(testo);
        b.setBackground(ORANGE);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(ORANGE_HO); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(ORANGE); }
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
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(40,40,40)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(DARK_BG); }
        });
        return b;
    }
}
