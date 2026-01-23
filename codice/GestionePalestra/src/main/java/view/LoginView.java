package view;

import action.LoginActions;
import action.LoginViewContract;
import action.ResetPasswordData;
import view.dialog.ThemedDialog;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginView extends JFrame implements LoginViewContract {

    private static final long serialVersionUID = 1L;

    private static final String LOGO_PATH = "/immagini/logo.png";

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrati;

    private LoginActions controller;

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    public LoginView() {
        initUI();
    }

    @Override
    public void setController(LoginActions controller) {
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

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(DARK_BG);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setPreferredSize(new Dimension(90, 90));
        logoLabel.setMaximumSize(new Dimension(90, 90));
        caricaLogo(logoLabel);

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
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (controller != null) controller.handlePasswordDimenticata();
            }
        });

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        cardPanel.add(lblForgot, gbc);

        centerPanel.add(cardPanel);
        centerPanel.add(Box.createVerticalStrut(20));

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

        btnLogin.addActionListener(e -> onLoginClicked());
        btnRegistrati.addActionListener(e -> onRegistratiClicked());
    }

    private void caricaLogo(JLabel logoLabel) {
        URL logoUrl = LoginView.class.getResource(LOGO_PATH);
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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(40, 40, 40)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(DARK_BG); }
        });
        return b;
    }

    private void onLoginClicked() {
        if (controller == null) {
            ThemedDialog.showMessage(this, "Errore", "Controller non impostato!", true);
            return;
        }
        controller.handleLogin(
                txtUsername.getText().trim(),
                new String(txtPassword.getPassword())
        );
    }

    private void onRegistratiClicked() {
        if (controller == null) {
            ThemedDialog.showMessage(this, "Errore", "Controller non impostato!", true);
            return;
        }
        controller.handleRegistrazione();
    }

    @Override
    public void mostraMessaggioInfo(String msg) {
        ThemedDialog.showMessage(this, "Info", msg, false);
    }

    @Override
    public void mostraMessaggioErrore(String msg) {
        ThemedDialog.showMessage(this, "Errore", msg, true);
    }

    // ====================== DIALOG RESET PASSWORD ======================

    private static void stylePrimaryButtonDialog(JButton b) {
        b.setBackground(ORANGE);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(ORANGE_HO); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(ORANGE); }
        });
    }

    private static void styleSecondaryButtonDialog(JButton b) {
        b.setBackground(DARK_BG);
        b.setForeground(ORANGE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(ORANGE));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(40,40,40)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(DARK_BG); }
        });
    }

    @Override
    public String chiediEmailReset() {
        final JDialog dialog = new JDialog(
                this,
                "Password dimenticata",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitolo = new JLabel("Inserisci l'email associata all'account:");
        lblTitolo.setForeground(Color.WHITE);
        lblTitolo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTextField txtEmail = new JTextField(22);
        txtEmail.setBackground(new Color(40,40,40));
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setCaretColor(Color.WHITE);
        txtEmail.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(DARK_BG);
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(lblTitolo);
        center.add(Box.createVerticalStrut(8));
        center.add(txtEmail);

        JButton btnAnnulla = new JButton("Annulla");
        JButton btnOk      = new JButton("OK");
        styleSecondaryButtonDialog(btnAnnulla);
        stylePrimaryButtonDialog(btnOk);

        final String[] result = new String[1];
        result[0] = null;

        btnAnnulla.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });
        btnOk.addActionListener(e -> {
            result[0] = txtEmail.getText().trim();
            dialog.dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(DARK_BG);
        bottom.add(btnAnnulla);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(btnOk);

        panel.add(center, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }

    @Override
    public ResetPasswordData chiediCodiceENuovaPassword() {
        final JDialog dialog = new JDialog(
                this,
                "Reimposta password",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblCod = new JLabel("Codice di reset:");
        lblCod.setForeground(Color.WHITE);
        JPasswordField txtCod = new JPasswordField(12);

        JLabel lblNew = new JLabel("Nuova password:");
        lblNew.setForeground(Color.WHITE);
        JPasswordField txtNew = new JPasswordField(15);

        JLabel lblConf = new JLabel("Conferma nuova password:");
        lblConf.setForeground(Color.WHITE);
        JPasswordField txtConf = new JPasswordField(15);

        for (JPasswordField f : new JPasswordField[]{txtCod, txtNew, txtConf}) {
            f.setBackground(new Color(40,40,40));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
            f.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        lblCod.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNew.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblConf.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblCod);
        panel.add(txtCod);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblNew);
        panel.add(txtNew);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblConf);
        panel.add(txtConf);

        JButton btnAnnulla = new JButton("Annulla");
        JButton btnOk      = new JButton("OK");
        styleSecondaryButtonDialog(btnAnnulla);
        stylePrimaryButtonDialog(btnOk);

        final ResetPasswordData[] result = new ResetPasswordData[1];
        result[0] = null;

        btnAnnulla.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });
        btnOk.addActionListener(e -> {
            result[0] = new ResetPasswordData(
                    new String(txtCod.getPassword()).trim(),
                    new String(txtNew.getPassword()),
                    new String(txtConf.getPassword())
            );
            dialog.dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(DARK_BG);
        bottom.add(btnAnnulla);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(btnOk);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DARK_BG);
        root.add(panel, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }
}
