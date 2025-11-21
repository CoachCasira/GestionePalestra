package view;

import controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;

    // Percorso corretto nel classpath (senza spazi!)
    private static final String LOGO_PATH = "/immagini/logo.png";

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrati;

    private LoginController controller;

    // Palette colori
    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    public LoginView() {
        initUI();
    }

    public void setController(LoginController controller) {
        this.controller = controller;
    }

    private void initUI() {
        setTitle("GestionePalestra - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        setContentPane(mainPanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        centerPanel.setBackground(DARK_BG);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ---------------- HEADER ----------------
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(DARK_BG);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setPreferredSize(new Dimension(90, 90));
        logoLabel.setMaximumSize(new Dimension(90, 90));
        caricaLogo(logoLabel); // carico immagine senza bordo

        JLabel lblTitolo = new JLabel("GESTIONEPALESTRA");
        lblTitolo.setForeground(ORANGE);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSottotitolo1 = new JLabel("Inserisci le tue");
        lblSottotitolo1.setForeground(TEXT_GRAY);
        lblSottotitolo1.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSottotitolo1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSottotitolo2 = new JLabel("credenziali di accesso");
        lblSottotitolo2.setForeground(Color.WHITE);
        lblSottotitolo2.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblSottotitolo2.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(lblTitolo);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblSottotitolo1);
        headerPanel.add(lblSottotitolo2);
        headerPanel.add(Box.createVerticalStrut(20));

        centerPanel.add(headerPanel);

        // ---------------- CARD FORM ----------------
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(TEXT_GRAY);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(TEXT_GRAY);
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 11));

        txtUsername = new JTextField(18);
        txtUsername.setBackground(new Color(40, 40, 40));
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        txtPassword = new JPasswordField(18);
        txtPassword.setBackground(new Color(40, 40, 40));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        cardPanel.add(lblUser, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        cardPanel.add(txtUsername, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        cardPanel.add(lblPass, gbc); row++;
        gbc.gridx = 0; gbc.gridy = row;
        cardPanel.add(txtPassword, gbc); row++;
        JLabel lblForgot = new JLabel("Password dimenticata?");
        lblForgot.setForeground(ORANGE);
        lblForgot.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        cardPanel.add(lblForgot, gbc);

        centerPanel.add(cardPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // ---------------- BOTTONI ----------------
        btnLogin = creaBottoneArancione("Accedi");
        btnRegistrati = creaBottoneSoloBordo("Crea account");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(DARK_BG);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrati.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonsPanel.add(btnLogin);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnRegistrati);

        centerPanel.add(buttonsPanel);
        centerPanel.add(Box.createVerticalGlue());

        // ---------------- FOOTER ----------------
        JLabel lblFooter = new JLabel(
                "Accedendo, accetti i termini di servizio e l'informativa privacy.",
                SwingConstants.CENTER);
        lblFooter.setForeground(new Color(160, 160, 160));
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblFooter.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(DARK_BG);
        footerPanel.add(lblFooter, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Listener bottoni
        btnLogin.addActionListener(e -> onLoginClicked());
        btnRegistrati.addActionListener(e -> onRegistratiClicked());
    }

    /** Carica il logo correttamente senza bordo */
    private void caricaLogo(JLabel logoLabel) {
        URL logoUrl = LoginView.class.getResource(LOGO_PATH);
        System.out.println("URL logo: " + logoUrl);

        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                Image scaled = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaled));
                logoLabel.setText(null);
            } else {
                logoLabel.setText("LOGO");
                logoLabel.setForeground(Color.WHITE);
            }
        } else {
            logoLabel.setText("LOGO");
            logoLabel.setForeground(Color.WHITE);
        }
    }

    // ---------------- Metodi bottoni con hover ----------------
    private JButton creaBottoneArancione(String testo) {
        JButton b = new JButton(testo);
        b.setPreferredSize(new Dimension(220, 40));
        b.setMaximumSize(new Dimension(260, 40));
        b.setBackground(ORANGE);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(ORANGE_HO); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(ORANGE); }
        });
        return b;
    }

    private JButton creaBottoneSoloBordo(String testo) {
        JButton b = new JButton(testo);
        b.setPreferredSize(new Dimension(220, 36));
        b.setMaximumSize(new Dimension(260, 36));
        b.setBackground(DARK_BG);
        b.setForeground(ORANGE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(ORANGE));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(40, 40, 40)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(DARK_BG); }
        });
        return b;
    }

    // ---------------- Callback listener ----------------
    private void onLoginClicked() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Controller non impostato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        controller.handleLogin(txtUsername.getText().trim(), new String(txtPassword.getPassword()));
    }

    private void onRegistratiClicked() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Controller non impostato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        controller.handleRegistrazione();
    }

    // ---------------- Utility controller ----------------
    public void mostraMessaggioInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostraMessaggioErrore(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}
