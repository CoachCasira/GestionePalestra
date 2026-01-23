package view;

import action.HomeAction;
import model.Abbonamento;
import model.Cliente;
import view.dialog.ThemedDialog;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class HomeView extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color CARD_INNER_BG = new Color(35, 35, 35);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    private final Cliente cliente;

    // al posto del controller concreto: interfaccia azioni
    private HomeAction action;

    private JButton btnVediAbbonamento;
    private JButton btnPrenotaCorso;
    private JButton btnPrenotaConsulenza;
    private JButton btnVediConsulenza;
    private JButton btnVediCorsi;
    private JButton btnDisdiciAbbonamento;
    private JButton btnLogout;

    private JButton btnPanoramicaPalestra;

    public HomeView(Cliente cliente) {
        this.cliente = cliente;
        initUI();
    }

    public void setAction(HomeAction action) {
        this.action = action;
    }

    private void initUI() {
        setTitle("GestionePalestra - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(420, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        setContentPane(mainPanel);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel centerHeader = new JPanel();
        centerHeader.setOpaque(false);
        centerHeader.setLayout(new BoxLayout(centerHeader, BoxLayout.Y_AXIS));
        centerHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon logoIcon = caricaLogo();
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        }
        centerHeader.add(logoLabel);
        centerHeader.add(Box.createVerticalStrut(10));

        JLabel lblArea = new JLabel("Area personale");
        lblArea.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblArea.setForeground(ORANGE);
        lblArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        String benvenutoTxt = "Benvenuto, " + cliente.getNome() + " " + cliente.getCognome();
        JLabel lblWelcome = new JLabel(benvenutoTxt);
        lblWelcome.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerHeader.add(lblArea);
        centerHeader.add(Box.createVerticalStrut(4));
        centerHeader.add(lblWelcome);

        header.add(centerHeader, BorderLayout.CENTER);

        btnLogout = creaBottoneSoloBordo("Logout");
        btnLogout.setPreferredSize(new Dimension(120, 36));

        JPanel logoutWrapper = new JPanel();
        logoutWrapper.setOpaque(false);
        logoutWrapper.setLayout(new BoxLayout(logoutWrapper, BoxLayout.Y_AXIS));

        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutWrapper.add(btnLogout);
        logoutWrapper.add(Box.createVerticalGlue());

        header.add(logoutWrapper, BorderLayout.EAST);

        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);
        leftSpacer.setPreferredSize(new Dimension(
                btnLogout.getPreferredSize().width,
                btnLogout.getPreferredSize().height
        ));
        header.add(leftSpacer, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setBackground(DARK_BG);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        JLabel lblScelta = new JLabel("Scegli un'operazione:");
        lblScelta.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblScelta.setForeground(TEXT_GRAY);
        lblScelta.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollContent.add(Box.createVerticalStrut(5));
        scrollContent.add(lblScelta);
        scrollContent.add(Box.createVerticalStrut(15));

        btnVediAbbonamento    = creaBottoneArancione("Vedi abbonamento");
        btnPrenotaCorso       = creaBottoneArancione("Iscriviti corso");
        btnPrenotaConsulenza  = creaBottoneArancione("Prenota consulenza");
        btnVediConsulenza     = creaBottoneSoloBordo("Vedi consulenze prenotate");
        btnVediCorsi          = creaBottoneSoloBordo("Vedi corsi");
        btnDisdiciAbbonamento = creaBottoneSoloBordo("Disdici abbonamento");

        btnPanoramicaPalestra = creaBottoneArancione("Visione palestra");

        JPanel cardAbb = creaSectionPanel("Abbonamento");
        cardAbb.add(Box.createVerticalStrut(10));
        cardAbb.add(wrapButtonFullWidth(btnVediAbbonamento));
        cardAbb.add(Box.createVerticalStrut(8));
        cardAbb.add(wrapButtonFullWidth(btnDisdiciAbbonamento));
        cardAbb.add(Box.createVerticalStrut(4));

        JPanel cardCorsi = creaSectionPanel("Corsi");
        cardCorsi.add(Box.createVerticalStrut(10));
        cardCorsi.add(wrapButtonFullWidth(btnPrenotaCorso));
        cardCorsi.add(Box.createVerticalStrut(8));
        cardCorsi.add(wrapButtonFullWidth(btnVediCorsi));
        cardCorsi.add(Box.createVerticalStrut(4));

        JPanel cardCons = creaSectionPanel("Consulenze");
        cardCons.add(Box.createVerticalStrut(10));
        cardCons.add(wrapButtonFullWidth(btnPrenotaConsulenza));
        cardCons.add(Box.createVerticalStrut(8));
        cardCons.add(wrapButtonFullWidth(btnVediConsulenza));
        cardCons.add(Box.createVerticalStrut(4));

        JPanel cardPalestra = creaSectionPanel("Palestra");
        cardPalestra.add(Box.createVerticalStrut(10));
        cardPalestra.add(wrapButtonFullWidth(btnPanoramicaPalestra));
        cardPalestra.add(Box.createVerticalStrut(4));

        scrollContent.add(cardAbb);
        scrollContent.add(Box.createVerticalStrut(15));
        scrollContent.add(cardCorsi);
        scrollContent.add(Box.createVerticalStrut(15));
        scrollContent.add(cardCons);
        scrollContent.add(Box.createVerticalStrut(15));
        scrollContent.add(cardPalestra);
        scrollContent.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(
                scrollContent,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DARK_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel(
                "GestionePalestra – versione demo progetto",
                SwingConstants.CENTER);
        lblFooter.setForeground(new Color(160, 160, 160));
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblFooter.setBorder(BorderFactory.createEmptyBorder(6, 5, 6, 5));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(DARK_BG);
        footerPanel.add(lblFooter, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        aggiornaVisibilitaCorsi();

        // LISTENER: delega all’interfaccia, niente controller concreto
        btnVediAbbonamento.addActionListener(e -> { if (action != null) action.onVediAbbonamento(); });
        btnPrenotaCorso.addActionListener(e -> { if (action != null) action.onPrenotaCorso(); });
        btnPrenotaConsulenza.addActionListener(e -> { if (action != null) action.onPrenotaConsulenza(); });
        btnVediConsulenza.addActionListener(e -> { if (action != null) action.onVediConsulenza(); });
        btnVediCorsi.addActionListener(e -> { if (action != null) action.onVediCorsi(); });
        btnDisdiciAbbonamento.addActionListener(e -> { if (action != null) action.onDisdiciAbbonamento(); });
        btnLogout.addActionListener(e -> { if (action != null) action.onLogout(); });
        btnPanoramicaPalestra.addActionListener(e -> { if (action != null) action.onPanoramicaPalestra(); });
    }

    private ImageIcon caricaLogo() {
        try {
            URL url = getClass().getClassLoader().getResource("immagini/logo.png");
            if (url == null) return null;

            Image img = new ImageIcon(url).getImage();
            Image scaled = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void aggiornaVisibilitaCorsi() {
        Abbonamento abb = cliente != null ? cliente.getAbbonamento() : null;
        boolean haCorsi = abb != null &&
                abb.getTipo() != null &&
                abb.getTipo().equalsIgnoreCase("CORSI");

        btnPrenotaCorso.setEnabled(haCorsi);
        btnVediCorsi.setEnabled(haCorsi);

        if (!haCorsi) {
            btnPrenotaCorso.setToolTipText("Disponibile solo per abbonamento di tipo CORSI.");
            btnVediCorsi.setToolTipText("Disponibile solo per abbonamento di tipo CORSI.");
        } else {
            btnPrenotaCorso.setToolTipText(null);
            btnVediCorsi.setToolTipText(null);
        }
    }

    private JButton creaBottoneArancione(String testo) {
        JButton b = new JButton(testo);
        b.setPreferredSize(new Dimension(260, 44));
        b.setBackground(ORANGE);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { if (b.isEnabled()) b.setBackground(ORANGE_HO); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(ORANGE); }
        });
        return b;
    }

    private JButton creaBottoneSoloBordo(String testo) {
        JButton b = new JButton(testo);
        b.setPreferredSize(new Dimension(260, 40));
        b.setBackground(DARK_BG);
        b.setForeground(ORANGE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(ORANGE));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { if (b.isEnabled()) b.setBackground(new Color(40, 40, 40)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(DARK_BG); }
        });
        return b;
    }

    private JPanel creaSectionPanel(String titolo) {
        JPanel p = new JPanel();
        p.setBackground(CARD_INNER_BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(titolo);
        lbl.setForeground(ORANGE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 80, 80));
        sep.setBackground(new Color(80, 80, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel sepWrapper = new JPanel();
        sepWrapper.setOpaque(false);
        sepWrapper.setLayout(new BoxLayout(sepWrapper, BoxLayout.X_AXIS));
        sepWrapper.add(sep);

        p.add(sepWrapper);

        return p;
    }

    private JComponent wrapButtonFullWidth(JButton b) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        p.add(Box.createHorizontalGlue());
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(b);
        p.add(Box.createHorizontalGlue());
        return p;
    }

    public void mostraDettaglioAbbonamento(Abbonamento abb) {
        this.setVisible(false);

        JDialog dialog = new JDialog(this, "Dettagli abbonamento", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(420, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        dialog.setContentPane(main);

        JPanel header = new JPanel();
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Dettagli abbonamento");
        lblTitle.setForeground(ORANGE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        main.add(header, BorderLayout.NORTH);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBackground(CARD_BG);
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final String fullText = abb.getDescrizioneCompleta();
        txt.setText("");

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(DARK_BG);
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        center.add(scroll, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(DARK_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JButton btnChiudi = creaBottoneSoloBordo("Chiudi");
        btnChiudi.addActionListener(e -> dialog.dispose());
        footer.add(btnChiudi);

        main.add(footer, BorderLayout.SOUTH);

        final Timer[] timerRef = new Timer[1];
        timerRef[0] = new Timer(18, e -> {
            if (!dialog.isShowing()) {
                timerRef[0].stop();
                return;
            }
            int lenCorrente = txt.getText().length();
            if (lenCorrente < fullText.length()) {
                txt.append(String.valueOf(fullText.charAt(lenCorrente)));
                txt.setCaretPosition(txt.getDocument().getLength());
            } else {
                timerRef[0].stop();
            }
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
                HomeView.this.setVisible(true);
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
            }
        });

        timerRef[0].start();
        dialog.setVisible(true);
    }

    public void mostraDettaglioConsulenza(String testoDettaglio) {
        this.setVisible(false);

        JDialog dialog = new JDialog(this, "Dettagli consulenza", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(420, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        dialog.setContentPane(main);

        JPanel header = new JPanel();
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Dettagli consulenze prenotate");
        lblTitle.setForeground(ORANGE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        main.add(header, BorderLayout.NORTH);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBackground(CARD_BG);
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final String fullText = (testoDettaglio != null) ? testoDettaglio : "";
        txt.setText("");

        JScrollPane scroll = new JScrollPane(
                txt,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(DARK_BG);
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        center.add(scroll, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        footer.setBackground(DARK_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JButton btnDisdici = creaBottoneSoloBordo("Disdici consulenza...");
        JButton btnChiudi  = creaBottoneSoloBordo("Chiudi");

        Dimension btnSize = new Dimension(160, 40);
        btnDisdici.setPreferredSize(btnSize);
        btnChiudi.setPreferredSize(btnSize);

        footer.add(btnDisdici);
        footer.add(btnChiudi);

        main.add(footer, BorderLayout.SOUTH);

        final Timer[] timerRef = new Timer[1];
        timerRef[0] = new Timer(18, e -> {
            if (!dialog.isShowing()) {
                timerRef[0].stop();
                return;
            }
            int lenCorrente = txt.getText().length();
            if (lenCorrente < fullText.length()) {
                txt.append(String.valueOf(fullText.charAt(lenCorrente)));
                txt.setCaretPosition(txt.getDocument().getLength());
            } else {
                timerRef[0].stop();
            }
        });

        btnChiudi.addActionListener(e -> dialog.dispose());

        btnDisdici.addActionListener(e -> {
            if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
            dialog.dispose();
            if (action != null) action.onApriDisdettaConsulenza();
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
                HomeView.this.setVisible(true);
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
            }
        });

        timerRef[0].start();
        dialog.setVisible(true);
    }

    public void mostraDettaglioCorsi(String testoDettaglio) {
        this.setVisible(false);

        JDialog dialog = new JDialog(this, "Dettagli corsi", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(420, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        dialog.setContentPane(main);

        JPanel header = new JPanel();
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Dettagli corsi");
        lblTitle.setForeground(ORANGE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        main.add(header, BorderLayout.NORTH);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBackground(CARD_BG);
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final String fullText = testoDettaglio != null ? testoDettaglio : "";
        txt.setText("");

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(DARK_BG);
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        center.add(scroll, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        footer.setBackground(DARK_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JButton btnDisdici = creaBottoneSoloBordo("Disiscriviti da un corso...");
        JButton btnChiudi  = creaBottoneSoloBordo("Chiudi");

        Dimension btnSize = new Dimension(180, 40);
        btnDisdici.setPreferredSize(btnSize);
        btnChiudi.setPreferredSize(btnSize);

        footer.add(btnDisdici);
        footer.add(btnChiudi);

        main.add(footer, BorderLayout.SOUTH);

        final Timer[] timerRef = new Timer[1];
        timerRef[0] = new Timer(18, e -> {
            if (!dialog.isShowing()) {
                timerRef[0].stop();
                return;
            }
            int lenCorrente = txt.getText().length();
            if (lenCorrente < fullText.length()) {
                txt.append(String.valueOf(fullText.charAt(lenCorrente)));
                txt.setCaretPosition(txt.getDocument().getLength());
            } else {
                timerRef[0].stop();
            }
        });

        btnChiudi.addActionListener(e -> dialog.dispose());

        btnDisdici.addActionListener(e -> {
            if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
            dialog.dispose();
            if (action != null) action.onApriDisdettaCorso();
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
                HomeView.this.setVisible(true);
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
            }
        });

        timerRef[0].start();
        dialog.setVisible(true);
    }

    public void mostraPanoramicaPalestra(String testoDettaglio) {
        this.setVisible(false);

        JDialog dialog = new JDialog(this, "Panoramica palestra", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(420, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(DARK_BG);
        dialog.setContentPane(main);

        JPanel header = new JPanel();
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Panoramica palestra");
        lblTitle.setForeground(ORANGE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitle);
        main.add(header, BorderLayout.NORTH);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBackground(CARD_BG);
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final String fullText = testoDettaglio != null ? testoDettaglio : "";
        txt.setText("");

        JScrollPane scroll = new JScrollPane(txt);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(DARK_BG);
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        center.add(scroll, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(DARK_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JButton btnChiudi = creaBottoneSoloBordo("Chiudi");
        btnChiudi.addActionListener(e -> dialog.dispose());
        footer.add(btnChiudi);

        main.add(footer, BorderLayout.SOUTH);

        final Timer[] timerRef = new Timer[1];
        timerRef[0] = new Timer(18, e -> {
            if (!dialog.isShowing()) {
                timerRef[0].stop();
                return;
            }
            int lenCorrente = txt.getText().length();
            if (lenCorrente < fullText.length()) {
                txt.append(String.valueOf(fullText.charAt(lenCorrente)));
                txt.setCaretPosition(txt.getDocument().getLength());
            } else {
                timerRef[0].stop();
            }
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
                HomeView.this.setVisible(true);
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timerRef[0] != null && timerRef[0].isRunning()) timerRef[0].stop();
            }
        });

        timerRef[0].start();
        dialog.setVisible(true);
    }

    public void mostraMessaggioInfo(String msg) {
        ThemedDialog.showMessage(this, "Info", msg, false);
    }

    public void mostraMessaggioErrore(String msg) {
        ThemedDialog.showMessage(this, "Errore", msg, true);
    }

    public Cliente getCliente() {
        return cliente;
    }
}
