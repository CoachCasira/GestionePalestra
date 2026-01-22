package view;

import controller.RegistrazioneController;
import view.dialog.ThemedDialog;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class RegistrazioneView extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String LOGO_PATH = "/immagini/logo.png";

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtEmail;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtCF;
    private JTextField txtLuogoNascita;
    private JTextField txtIban;
    private JTextField txtDataNascita;

    private JButton btnConferma;
    private JButton btnAnnulla;

    private RegistrazioneController controller;

    public RegistrazioneView() {
        initUI();
    }

    private void initUI() {
        setTitle("Registrazione nuovo cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        setContentPane(mainPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(DARK_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        caricaLogo(logoLabel);

        JLabel lblTitolo = new JLabel("CREA IL TUO ACCOUNT");
        lblTitolo.setForeground(ORANGE);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSottotitolo = new JLabel("Inserisci i dati del nuovo cliente");
        lblSottotitolo.setForeground(TEXT_GRAY);
        lblSottotitolo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSottotitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(lblTitolo);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblSottotitolo);
        headerPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(DARK_BG);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        JLabel lblUsername = creaLabelCampo("Username");
        txtUsername = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblUsername, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtUsername, gbc);

        JLabel lblEmail = creaLabelCampo("Email");
        txtEmail = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblEmail, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtEmail, gbc);

        JLabel lblPassword = creaLabelCampo("Password");
        txtPassword = new JPasswordField(20);
        styleField(txtPassword);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblPassword, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtPassword, gbc);

        JLabel lblNome = creaLabelCampo("Nome");
        txtNome = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblNome, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtNome, gbc);

        JLabel lblCognome = creaLabelCampo("Cognome");
        txtCognome = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblCognome, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtCognome, gbc);

        JLabel lblCF = creaLabelCampo("Codice fiscale");
        txtCF = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblCF, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtCF, gbc);

        JLabel lblLuogo = creaLabelCampo("Luogo di nascita");
        txtLuogoNascita = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblLuogo, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtLuogoNascita, gbc);

        JLabel lblData = creaLabelCampo("Data di nascita (yyyy-MM-dd)");
        txtDataNascita = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblData, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtDataNascita, gbc);

        JLabel lblIban = creaLabelCampo("IBAN");
        txtIban = creaTextField();
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(lblIban, gbc);
        gbc.gridx = 0; gbc.gridy = row++;
        cardPanel.add(txtIban, gbc);

        scrollContent.add(cardPanel);
        scrollContent.add(Box.createVerticalStrut(15));

        btnConferma = creaBottoneArancione("Conferma");
        btnAnnulla  = creaBottoneSoloBordo("Annulla");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(DARK_BG);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnConferma.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAnnulla.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonsPanel.add(btnConferma);
        buttonsPanel.add(Box.createVerticalStrut(10));
        buttonsPanel.add(btnAnnulla);

        scrollContent.add(buttonsPanel);
        scrollContent.add(Box.createVerticalStrut(10));

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(DARK_BG);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel(
                "I dati inseriti saranno trattati secondo l'informativa privacy.",
                SwingConstants.CENTER);
        lblFooter.setForeground(new Color(160, 160, 160));
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblFooter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(DARK_BG);
        footerPanel.add(lblFooter, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        btnConferma.addActionListener(e -> onConfermaClicked());
        btnAnnulla.addActionListener(e -> onAnnullaClicked());
    }

    private JLabel creaLabelCampo(String testo) {
        JLabel l = new JLabel(testo);
        l.setForeground(TEXT_GRAY);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        return l;
    }

    private JTextField creaTextField() {
        JTextField t = new JTextField(20);
        styleField(t);
        return t;
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(40, 40, 40));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    private void caricaLogo(JLabel logoLabel) {
        URL logoUrl = RegistrazioneView.class.getResource(LOGO_PATH);
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image scaled = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        }
    }

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
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(ORANGE); }
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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(40,40,40)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(DARK_BG); }
        });
        return b;
    }

    public void setController(RegistrazioneController controller) {
        this.controller = controller;
    }

    private void onConfermaClicked() {
        if (controller == null) {
            ThemedDialog.showMessage(this, "Errore", "Controller non impostato!", true);
            return;
        }

        String username     = txtUsername.getText().trim();
        String password     = new String(txtPassword.getPassword());
        String email        = txtEmail.getText().trim();
        String nome         = txtNome.getText().trim();
        String cognome      = txtCognome.getText().trim();
        String cf           = txtCF.getText().trim();
        String luogoNascita = txtLuogoNascita.getText().trim();
        String dataNascita  = txtDataNascita.getText().trim();
        String iban         = txtIban.getText().trim();

        controller.handleConferma(username, password, nome, cognome,
                cf, luogoNascita, dataNascita, iban, email);
    }

    private void onAnnullaClicked() {
        if (controller != null) {
            controller.handleAnnulla();
        } else {
            dispose();
        }
    }

    public void mostraMessaggioInfo(String msg) {
        ThemedDialog.showMessage(this, "Info", msg, false);
    }

    public void mostraMessaggioErrore(String msg) {
        ThemedDialog.showMessage(this, "Errore", msg, true);
    }
}
