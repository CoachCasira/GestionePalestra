package view;

import controller.PrenotaConsulenzaController;
import model.Cliente;

import javax.swing.*;
import java.awt.*;

public class PrenotaConsulenzaView extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    private final Cliente cliente;
    private PrenotaConsulenzaController controller;

    private JRadioButton rbPT;
    private JRadioButton rbNutri;
    private JRadioButton rbIstr;

    private JTextArea txtDescrizioneTipo;
    private JComboBox<String> comboDipendente;
    private JTextArea txtDescrizioneDipendente;
    private JTextField txtData;  // yyyy-MM-dd
    private JTextField txtOra;   // HH:mm
    private JTextArea txtNote;

    private JButton btnConferma;
    private JButton btnAnnulla;

    public PrenotaConsulenzaView(Cliente cliente) {
        this.cliente = cliente;
        initUI();
    }

    public void setController(PrenotaConsulenzaController controller) {
        this.controller = controller;
    }

    private void initUI() {
        setTitle("Prenota consulenza");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // stessa dimensione di Home / Login
        setSize(420, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        setContentPane(main);

        // ===== HEADER =======================================================
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel lblTitolo = new JLabel("Prenota una consulenza");
        lblTitolo.setForeground(ORANGE);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSotto = new JLabel("Seleziona il tipo di consulenza, la data, l'orario e il dipendente.");
        lblSotto.setForeground(TEXT_GRAY);
        lblSotto.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSotto.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitolo);
        header.add(Box.createVerticalStrut(5));
        header.add(lblSotto);

        main.add(header, BorderLayout.NORTH);

        // ===== CARD CENTRALE (scrollabile) ==================================
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // --- Tipo consulenza ---
        rbPT   = new JRadioButton("Consulenza con Personal Trainer");
        rbNutri= new JRadioButton("Consulenza con Nutrizionista");
        rbIstr = new JRadioButton("Consulenza con Istruttore di corso");

        ButtonGroup group = new ButtonGroup();
        group.add(rbPT); group.add(rbNutri); group.add(rbIstr);

        styleRadio(rbPT);
        styleRadio(rbNutri);
        styleRadio(rbIstr);

        gbc.gridx = 0; gbc.gridy = row++;
        card.add(rbPT, gbc);
        gbc.gridy = row++;
        card.add(rbNutri, gbc);
        gbc.gridy = row++;
        card.add(rbIstr, gbc);

        // --- Descrizione tipo (scroll, altezza fissa) ---
        txtDescrizioneTipo = new JTextArea(4, 30);
        txtDescrizioneTipo.setEditable(false);
        txtDescrizioneTipo.setLineWrap(true);
        txtDescrizioneTipo.setWrapStyleWord(true);
        txtDescrizioneTipo.setBackground(new Color(40, 40, 40));
        txtDescrizioneTipo.setForeground(Color.WHITE);
        txtDescrizioneTipo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollDescrTipo = new JScrollPane(
                txtDescrizioneTipo,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollDescrTipo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Dettagli consulenza",
                0, 0, new Font("SansSerif", Font.PLAIN, 11), TEXT_GRAY
        ));
        scrollDescrTipo.setPreferredSize(new Dimension(0, 110));
        scrollDescrTipo.getViewport().setBackground(new Color(40, 40, 40));

        gbc.gridy = row++;
        card.add(scrollDescrTipo, gbc);

        // --- Data, ora ---
        JLabel lblData = new JLabel("Data (yyyy-MM-dd):");
        lblData.setForeground(TEXT_GRAY);
        JLabel lblOra = new JLabel("Ora (HH:mm):");
        lblOra.setForeground(TEXT_GRAY);

        txtData = new JTextField(10);
        txtOra  = new JTextField(5);

        JPanel rigaDataOra = new JPanel(new GridLayout(2, 2, 10, 5));
        rigaDataOra.setBackground(CARD_BG);
        rigaDataOra.add(lblData);
        rigaDataOra.add(txtData);
        rigaDataOra.add(lblOra);
        rigaDataOra.add(txtOra);

        gbc.gridy = row++;
        card.add(rigaDataOra, gbc);

        // --- Dipendente + descrizione dipendente ---
        JLabel lblDip = new JLabel("Dipendente:");
        lblDip.setForeground(TEXT_GRAY);

        comboDipendente = new JComboBox<>();
        comboDipendente.setBackground(new Color(40, 40, 40));
        comboDipendente.setForeground(Color.WHITE);

        JPanel rigaDip = new JPanel(new BorderLayout(10,0));
        rigaDip.setBackground(CARD_BG);
        rigaDip.add(lblDip, BorderLayout.WEST);
        rigaDip.add(comboDipendente, BorderLayout.CENTER);

        gbc.gridy = row++;
        card.add(rigaDip, gbc);

        txtDescrizioneDipendente = new JTextArea(4, 30);
        txtDescrizioneDipendente.setEditable(false);
        txtDescrizioneDipendente.setLineWrap(true);
        txtDescrizioneDipendente.setWrapStyleWord(true);
        txtDescrizioneDipendente.setBackground(new Color(40, 40, 40));
        txtDescrizioneDipendente.setForeground(Color.WHITE);
        txtDescrizioneDipendente.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollDescrDip = new JScrollPane(
                txtDescrizioneDipendente,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollDescrDip.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Dettagli dipendente",
                0, 0, new Font("SansSerif", Font.PLAIN, 11), TEXT_GRAY
        ));
        scrollDescrDip.setPreferredSize(new Dimension(0, 110));
        scrollDescrDip.getViewport().setBackground(new Color(40, 40, 40));

        gbc.gridy = row++;
        card.add(scrollDescrDip, gbc);

        // --- Note opzionali (scroll) ---
        txtNote = new JTextArea(3, 30);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setBackground(new Color(40, 40, 40));
        txtNote.setForeground(Color.WHITE);
        txtNote.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollNote = new JScrollPane(
                txtNote,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollNote.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Note per il professionista (opzionali)",
                0, 0, new Font("SansSerif", Font.PLAIN, 11), TEXT_GRAY
        ));
        scrollNote.setPreferredSize(new Dimension(0, 110));
        scrollNote.getViewport().setBackground(new Color(40, 40, 40));

        gbc.gridy = row++;
        card.add(scrollNote, gbc);

        // Scrollpane che contiene tutta la card
        JScrollPane scrollCard = new JScrollPane(
                card,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollCard.setBorder(null);
        scrollCard.getViewport().setBackground(CARD_BG);
        main.add(scrollCard, BorderLayout.CENTER);

        // ===== FOOTER BOTTONI ===============================================
        JPanel buttons = new JPanel();
        buttons.setBackground(DARK_BG);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnConferma = creaBottoneArancione("Conferma prenotazione");
        btnAnnulla  = creaBottoneSoloBordo("Annulla / Indietro");

        buttons.add(btnConferma);
        buttons.add(Box.createHorizontalStrut(15));
        buttons.add(btnAnnulla);

        main.add(buttons, BorderLayout.SOUTH);

        // ===== LISTENER =====================================================
        rbPT.addActionListener(e -> {
            if (controller != null) controller.handleTipoSelezionato("PERSONAL_TRAINER");
        });
        rbNutri.addActionListener(e -> {
            if (controller != null) controller.handleTipoSelezionato("NUTRIZIONISTA");
        });
        rbIstr.addActionListener(e -> {
            if (controller != null) controller.handleTipoSelezionato("ISTRUTTORE_CORSO");
        });

        comboDipendente.addActionListener(e -> {
            if (controller != null) {
                String nome = getDipendenteSelezionato();
                controller.handleDipendenteSelezionato(nome);
            }
        });

        btnConferma.addActionListener(e -> {
            if (controller != null) controller.handleConfermaPrenotazione();
        });

        btnAnnulla.addActionListener(e -> {
            if (controller != null) controller.handleAnnulla();
        });

        // selezione di default
        rbPT.setSelected(true);
    }

    private void styleRadio(JRadioButton rb) {
        rb.setBackground(CARD_BG);
        rb.setForeground(TEXT_GRAY);
        rb.setFocusPainted(false);
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

    // ===== getter usati dal controller =====
    public String getTipoSelezionato() {
        if (rbNutri.isSelected()) return "NUTRIZIONISTA";
        if (rbIstr.isSelected())  return "ISTRUTTORE_CORSO";
        return "PERSONAL_TRAINER";
    }

    public String getDataText() {
        return txtData.getText().trim();
    }

    public String getOraText() {
        return txtOra.getText().trim();
    }

    public String getDipendenteSelezionato() {
        Object o = comboDipendente.getSelectedItem();
        return o != null ? o.toString() : null;
    }

    public String getNote() {
        return txtNote.getText().trim();
    }

    public void setDescrizioneTipo(String testo) {
        txtDescrizioneTipo.setText(testo);
        txtDescrizioneTipo.setCaretPosition(0);
    }

    public void setDipendenti(String[] nomi) {
        comboDipendente.removeAllItems();
        if (nomi != null) {
            for (String n : nomi) comboDipendente.addItem(n);
        }
    }

    public void setDescrizioneDipendente(String testo) {
        txtDescrizioneDipendente.setText(testo);
        txtDescrizioneDipendente.setCaretPosition(0);
    }

    public Cliente getCliente() {
        return cliente;
    }
}
