package view;

import controller.PrenotaCorsoController;
import model.Cliente;

import javax.swing.*;
import java.awt.*;

public class PrenotaCorsoView extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    private final Cliente cliente;
    private PrenotaCorsoController controller;

    private JList<String> listaCorsi;
    private DefaultListModel<String> modelCorsi;

    private JTextArea txtDescrizioneCorso;

    private JList<String> listaLezioni;
    private DefaultListModel<String> modelLezioni;

    private JButton btnConferma;
    private JButton btnAnnulla;

    public PrenotaCorsoView(Cliente cliente) {
        this.cliente = cliente;
        initUI();
    }

    public void setController(PrenotaCorsoController controller) {
        this.controller = controller;
    }

    private void initUI() {
        setTitle("Prenota corso");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        setContentPane(main);

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitolo = new JLabel("Prenota un corso");
        lblTitolo.setForeground(ORANGE);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Seleziona un corso, poi una data/ora disponibile e conferma.");
        lblSub.setForeground(TEXT_GRAY);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitolo);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        main.add(header, BorderLayout.NORTH);

        // CENTRO: due colonne
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 0));
        center.setBackground(DARK_BG);
        center.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // --- Colonna sinistra: corsi ---
        JPanel panelCorsi = new JPanel(new BorderLayout());
        panelCorsi.setBackground(CARD_BG);
        panelCorsi.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Corsi disponibili",
                0, 0, new Font("SansSerif", Font.PLAIN, 12), TEXT_GRAY));

        modelCorsi = new DefaultListModel<>();
        listaCorsi = new JList<>(modelCorsi);
        listaCorsi.setBackground(CARD_BG);
        listaCorsi.setForeground(Color.WHITE);
        listaCorsi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCorsi.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane scrollCorsi = new JScrollPane(listaCorsi);
        scrollCorsi.setBorder(null);
        scrollCorsi.getViewport().setBackground(CARD_BG);

        txtDescrizioneCorso = new JTextArea(5, 20);
        txtDescrizioneCorso.setEditable(false);
        txtDescrizioneCorso.setLineWrap(true);
        txtDescrizioneCorso.setWrapStyleWord(true);
        txtDescrizioneCorso.setBackground(new Color(40, 40, 40));
        txtDescrizioneCorso.setForeground(Color.WHITE);
        txtDescrizioneCorso.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDescrizioneCorso.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JScrollPane scrollDescr = new JScrollPane(txtDescrizioneCorso);
        scrollDescr.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Descrizione corso",
                0, 0, new Font("SansSerif", Font.PLAIN, 11), TEXT_GRAY));
        scrollDescr.setPreferredSize(new Dimension(0, 120));
        scrollDescr.getViewport().setBackground(new Color(40, 40, 40));

        panelCorsi.add(scrollCorsi, BorderLayout.CENTER);
        panelCorsi.add(scrollDescr, BorderLayout.SOUTH);

        // --- Colonna destra: lezioni ---
        JPanel panelLezioni = new JPanel(new BorderLayout());
        panelLezioni.setBackground(CARD_BG);
        panelLezioni.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                "Date / orari disponibili",
                0, 0, new Font("SansSerif", Font.PLAIN, 12), TEXT_GRAY));

        modelLezioni = new DefaultListModel<>();
        listaLezioni = new JList<>(modelLezioni);
        listaLezioni.setBackground(CARD_BG);
        listaLezioni.setForeground(Color.WHITE);
        listaLezioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaLezioni.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane scrollLezioni = new JScrollPane(listaLezioni);
        scrollLezioni.setBorder(null);
        scrollLezioni.getViewport().setBackground(CARD_BG);

        panelLezioni.add(scrollLezioni, BorderLayout.CENTER);

        center.add(panelCorsi);
        center.add(panelLezioni);

        main.add(center, BorderLayout.CENTER);

        // FOOTER bottoni
        JPanel footer = new JPanel();
        footer.setBackground(DARK_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        btnConferma = creaBottoneArancione("Conferma iscrizione");
        btnAnnulla  = creaBottoneSoloBordo("Annulla / Indietro");

        footer.add(btnConferma);
        footer.add(Box.createHorizontalStrut(15));
        footer.add(btnAnnulla);

        main.add(footer, BorderLayout.SOUTH);

        // LISTENER
        listaCorsi.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && controller != null) {
                int idx = listaCorsi.getSelectedIndex();
                controller.handleCorsoSelezionato(idx);
            }
        });

        btnConferma.addActionListener(e -> {
            if (controller != null) controller.handleConfermaIscrizione();
        });

        btnAnnulla.addActionListener(e -> {
            if (controller != null) controller.handleAnnulla();
        });
    }

    // ========= factory bottoni =========
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

    // ========= API usate dal controller =========
    public Cliente getCliente() {
        return cliente;
    }

    public void setCorsi(String[] nomi) {
        modelCorsi.clear();
        if (nomi != null) {
            for (String n : nomi) modelCorsi.addElement(n);
        }
        listaCorsi.clearSelection();
        modelLezioni.clear();
        txtDescrizioneCorso.setText("");
    }

    public void setDescrizioneCorso(String testo) {
        txtDescrizioneCorso.setText(testo);
        txtDescrizioneCorso.setCaretPosition(0);
    }

    public void setLezioni(String[] righe) {
        modelLezioni.clear();
        if (righe != null) {
            for (String r : righe) modelLezioni.addElement(r);
        }
        listaLezioni.clearSelection();
    }

    public int getIndiceCorsoSelezionato() {
        return listaCorsi.getSelectedIndex();
    }

    public int getIndiceLezioneSelezionata() {
        return listaLezioni.getSelectedIndex();
    }
}
