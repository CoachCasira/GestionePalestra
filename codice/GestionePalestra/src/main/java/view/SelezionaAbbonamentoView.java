package view;

import action.SelezionaAbbonamentoActions;
import action.SelezionaAbbonamentoViewContract;
import model.Cliente;
import view.dialog.ThemedDialog;

import javax.swing.*;
import java.awt.*;

public class SelezionaAbbonamentoView extends JFrame implements SelezionaAbbonamentoViewContract {

    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    // Dimensioni "telefono-like" allineate alla Home
    private static final int FRAME_W = 420;
    private static final int FRAME_H = 650;

    private JRadioButton rbBase;
    private JRadioButton rbCompleto;
    private JRadioButton rbCorsi;
    private JButton btnProcedi;
    private JButton btnAnnulla;

    private JTextArea txtDettagli;

    private SelezionaAbbonamentoActions actions;

    // Costruttore usato normalmente
    public SelezionaAbbonamentoView() {
        initUI();
    }

    // Costruttore compatibile con: new SelezionaAbbonamentoView(cliente)
    public SelezionaAbbonamentoView(Cliente cliente) {
        this();
    }

    @Override
    public void setActions(SelezionaAbbonamentoActions actions) {
        this.actions = actions;
    }

    private void initUI() {
        setTitle("GestionePalestra - Scelta abbonamento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Allineamento dimensioni con HomeView
        setSize(FRAME_W, FRAME_H);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        setContentPane(main);

        // HEADER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTitolo = new JLabel("Scegli il tuo abbonamento");
        lblTitolo.setForeground(ORANGE);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSotto = new JLabel("Puoi selezionare un solo tipo di abbonamento.");
        lblSotto.setForeground(TEXT_GRAY);
        lblSotto.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSotto.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitolo);
        header.add(Box.createVerticalStrut(5));
        header.add(lblSotto);

        main.add(header, BorderLayout.NORTH);

        // CENTRO: scelta + dettagli
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(DARK_BG);
        main.add(center, BorderLayout.CENTER);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        rbBase = new JRadioButton("Abbonamento BASE");
        rbCompleto = new JRadioButton("Abbonamento COMPLETO");
        rbCorsi = new JRadioButton("Abbonamento CORSI");

        ButtonGroup group = new ButtonGroup();
        group.add(rbBase);
        group.add(rbCompleto);
        group.add(rbCorsi);

        styleRadio(rbBase);
        styleRadio(rbCompleto);
        styleRadio(rbCorsi);

        card.add(rbBase, gbc); gbc.gridy++;
        card.add(rbCompleto, gbc); gbc.gridy++;
        card.add(rbCorsi, gbc); gbc.gridy++;

        // Text area per dettagli
        txtDettagli = new JTextArea(6, 30);
        txtDettagli.setEditable(false);
        txtDettagli.setLineWrap(true);
        txtDettagli.setWrapStyleWord(true);
        txtDettagli.setBackground(new Color(40, 40, 40));
        txtDettagli.setForeground(Color.WHITE);
        txtDettagli.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDettagli.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Dettagli abbonamento selezionato",
                0, 0, new Font("SansSerif", Font.PLAIN, 11), TEXT_GRAY
        ));

        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        card.add(txtDettagli, gbc);

        center.add(card);

        // FOOTER: bottoni
        JPanel buttons = new JPanel();
        buttons.setBackground(DARK_BG);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnProcedi = creaBottoneArancione("Procedi al pagamento");
        btnProcedi.setEnabled(false);

        btnAnnulla = creaBottoneSoloBordo("Annulla / Logout");

        buttons.add(btnProcedi);
        buttons.add(Box.createHorizontalStrut(15));
        buttons.add(btnAnnulla);

        main.add(buttons, BorderLayout.SOUTH);

        // Listener
        rbBase.addActionListener(e -> aggiornaDettagli("BASE"));
        rbCompleto.addActionListener(e -> aggiornaDettagli("COMPLETO"));
        rbCorsi.addActionListener(e -> aggiornaDettagli("CORSI"));

        btnProcedi.addActionListener(e -> {
            if (actions != null) {
                String tipo = getTipoSelezionato();
                if (tipo != null) actions.onProcedi(tipo);
            }
        });

        btnAnnulla.addActionListener(e -> {
            if (actions != null) actions.onAnnulla();
        });
    }

    private void styleRadio(JRadioButton rb) {
        rb.setBackground(CARD_BG);
        rb.setForeground(TEXT_GRAY);
        rb.setFocusPainted(false);
    }

    private void aggiornaDettagli(String tipo) {
        btnProcedi.setEnabled(true);
        if ("BASE".equals(tipo)) {
            txtDettagli.setText(
                    "ABBONAMENTO BASE\n\n" +
                    "- Accesso alla sala pesi negli orari standard\n" +
                    "- Nessun accesso ai corsi di gruppo\n" +
                    "- Prezzo indicativo: 30 €/mese");
        } else if ("COMPLETO".equals(tipo)) {
            txtDettagli.setText(
                    "ABBONAMENTO COMPLETO\n\n" +
                    "- Accesso illimitato alla sala pesi\n" +
                    "- Accesso a tutti i corsi di gruppo\n" +
                    "- Utilizzo spogliatoio dedicato\n" +
                    "- Prezzo indicativo: 50 €/mese");
        } else if ("CORSI".equals(tipo)) {
            txtDettagli.setText(
                    "ABBONAMENTO CORSI\n\n" +
                    "- Accesso ai corsi di gruppo\n" +
                    "- Accesso limitato alla sala pesi\n" +
                    "- Prezzo indicativo: 40 €/mese");
        }
    }

    private String getTipoSelezionato() {
        if (rbBase.isSelected()) return "BASE";
        if (rbCompleto.isSelected()) return "COMPLETO";
        if (rbCorsi.isSelected()) return "CORSI";
        return null;
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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(40, 40, 40)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(DARK_BG); }
        });
        return b;
    }

    @Override
    public void mostraMessaggioInfo(String msg) {
        ThemedDialog.showMessage(this, "Info", msg, false);
    }

    @Override
    public void mostraMessaggioErrore(String msg) {
        ThemedDialog.showMessage(this, "Errore", msg, true);
    }

    @Override
    public void close() {
        dispose();
    }

    @Override
    public void showView() {
        setVisible(true);
    }
}
