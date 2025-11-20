package view;

import javax.swing.*;
import java.awt.*;
import controller.LoginController;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistrati;

    private LoginController controller;

    public LoginView() {
        initUI();
    }

    // Il controller verrà impostato dal main
    public void setController(LoginController controller) {
        this.controller = controller;
    }

    private void initUI() {
        setTitle("GestionePalestra - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Finestra più grande
        setSize(500, 400);
        setLocationRelativeTo(null); // centra la finestra

        // Palette colori (solo per pannelli e label, NON per i JButton)
        Color primary = new Color(24, 90, 157);      // blu
        Color bg      = new Color(240, 240, 240);    // grigio chiaro

        // Pannello principale con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bg);
        setContentPane(mainPanel);

        // ----- HEADER (titolo) -----
        JLabel lblTitolo = new JLabel("Benvenuto in GestionePalestra", SwingConstants.CENTER);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitolo.setForeground(primary);
        lblTitolo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(lblTitolo, BorderLayout.NORTH);

        // ----- FORM CENTRALE -----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);

        // riga 0 - username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(lblUsername, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1;
        formPanel.add(txtUsername, gbc);

        // riga 1 - password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1;
        formPanel.add(txtPassword, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ----- FOOTER CON BOTTONI -----
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bg);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        btnLogin = new JButton("Accedi");
        btnRegistrati = new JButton("Registrati");

        // niente setForeground / setBackground sui bottoni → li gestisce il sistema
        buttonPanel.add(btnLogin);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(btnRegistrati);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Listener dei bottoni
        btnLogin.addActionListener(e -> onLoginClicked());
        btnRegistrati.addActionListener(e -> onRegistratiClicked());
    }

    private void onLoginClicked() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller non impostato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        controller.handleLogin(username, password);
    }

    private void onRegistratiClicked() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller non impostato!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controller.handleRegistrazione();
    }

    // Metodi di comodo per mostrare messaggi dal controller
    public void mostraMessaggioInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostraMessaggioErrore(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}
