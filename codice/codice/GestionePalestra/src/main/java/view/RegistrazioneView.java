package view;

import controller.RegistrazioneController;

import javax.swing.*;
import java.awt.*;

public class RegistrazioneView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtCF;
    private JTextField txtLuogoNascita;
    private JTextField txtIban;
    private JTextField txtDataNascita; // per ora String (yyyy-MM-dd, ad esempio)

    private JButton btnConferma;
    private JButton btnAnnulla;

    private RegistrazioneController controller;

    public RegistrazioneView() {
        setTitle("Registrazione nuovo cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Username
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Username:"), gbc);
        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Password:"), gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        row++;

        // Nome
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Nome:"), gbc);
        txtNome = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtNome, gbc);
        row++;

        // Cognome
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Cognome:"), gbc);
        txtCognome = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtCognome, gbc);
        row++;

        // CF
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Codice Fiscale:"), gbc);
        txtCF = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtCF, gbc);
        row++;

        // Luogo nascita
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Luogo di nascita:"), gbc);
        txtLuogoNascita = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtLuogoNascita, gbc);
        row++;

        // Data nascita (stringa per ora)
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Data di nascita (yyyy-MM-dd):"), gbc);
        txtDataNascita = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtDataNascita, gbc);
        row++;

        // IBAN
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("IBAN:"), gbc);
        txtIban = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtIban, gbc);
        row++;

        // Pulsanti
        gbc.gridx = 0; gbc.gridy = row;
        btnConferma = new JButton("Conferma");
        panel.add(btnConferma, gbc);

        gbc.gridx = 1;
        btnAnnulla = new JButton("Annulla");
        panel.add(btnAnnulla, gbc);

        // Listener
        btnConferma.addActionListener(e -> onConfermaClicked());
        btnAnnulla.addActionListener(e -> onAnnullaClicked());

        setContentPane(panel);
    }

    public void setController(RegistrazioneController controller) {
        this.controller = controller;
    }

    private void onConfermaClicked() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller non impostato!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String nome = txtNome.getText().trim();
        String cognome = txtCognome.getText().trim();
        String cf = txtCF.getText().trim();
        String luogoNascita = txtLuogoNascita.getText().trim();
        String dataNascita = txtDataNascita.getText().trim();
        String iban = txtIban.getText().trim();

        controller.handleConferma(username, password, nome, cognome,
                cf, luogoNascita, dataNascita, iban);
    }

    private void onAnnullaClicked() {
        dispose();
    }

    // Metodi di comodo per messaggi dal controller
    public void mostraMessaggioInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostraMessaggioErrore(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Errore",
                JOptionPane.ERROR_MESSAGE);
    }
}
