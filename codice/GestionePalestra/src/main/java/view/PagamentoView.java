package view;

import action.PagamentoAction;
import action.PagamentoViewContract;
import model.Abbonamento;
import model.Cliente;
import view.dialog.ThemedDialog;

import javax.swing.*;
import java.awt.*;

public class PagamentoView extends JFrame implements PagamentoViewContract {

    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color CARD_BG   = new Color(30, 30, 30);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);
    private static final Color TEXT_GRAY = new Color(200, 200, 200);

    // Dimensioni "telefono-like" allineate alla Home
    private static final int FRAME_W = 420;
    private static final int FRAME_H = 650;

    private PagamentoAction action;

    private JLabel lblTipo;
    private JLabel lblPrezzo;
    private JLabel lblFascia;
    private JComboBox<String> comboMetodo;
    private JButton btnPaga;
    private JButton btnAnnulla;

    private final Cliente cliente;
    private final Abbonamento abbonamento;

    public PagamentoView(Cliente cliente, Abbonamento abbonamento) {
        this.cliente = cliente;
        this.abbonamento = abbonamento;
        initUI();
    }

    @Override
    public void setAction(PagamentoAction action) {
        this.action = action;
    }

    private void initUI() {
        setTitle("GestionePalestra - Pagamento abbonamento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Allineamento dimensioni con HomeView
        setSize(FRAME_W, FRAME_H);
        setLocationRelativeTo(null);
        setResizable(false);

        // Contenuto scrollabile (così non serve mai "allargare" la finestra)
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(DARK_BG);

        JScrollPane scrollPane = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DARK_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        setContentPane(scrollPane);

        // HEADER
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTitolo = new JLabel("Conferma pagamento");
        lblTitolo.setForeground(ORANGE);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSotto = new JLabel("Controlla i dati dell'abbonamento e scegli il metodo di pagamento.");
        lblSotto.setForeground(TEXT_GRAY);
        lblSotto.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSotto.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(lblTitolo);
        header.add(Box.createVerticalStrut(5));
        header.add(lblSotto);

        content.add(header, BorderLayout.NORTH);

        // CENTER (card)
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(DARK_BG);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        lblTipo = new JLabel("Tipo abbonamento: " + abbonamento.getTipo());
        lblTipo.setForeground(Color.WHITE);

        lblPrezzo = new JLabel("Prezzo: " + abbonamento.getPrezzo() + " €");
        lblPrezzo.setForeground(Color.WHITE);

        lblFascia = new JLabel("Fascia oraria: " + abbonamento.getFasciaOrariaConsentita());
        lblFascia.setForeground(Color.WHITE);

        gbc.gridy = 0;
        card.add(lblTipo, gbc);
        gbc.gridy++;
        card.add(lblPrezzo, gbc);
        gbc.gridy++;
        card.add(lblFascia, gbc);

        gbc.gridy++;
        JLabel lblMetodo = new JLabel("Metodo di pagamento:");
        lblMetodo.setForeground(TEXT_GRAY);
        card.add(lblMetodo, gbc);

        gbc.gridy++;
        comboMetodo = new JComboBox<>(new String[]{"Carta di credito", "Bancomat", "Contanti"});
        comboMetodo.setSelectedIndex(0);
        comboMetodo.setBackground(Color.WHITE);
        comboMetodo.setForeground(Color.BLACK);
        comboMetodo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboMetodo.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        card.add(comboMetodo, gbc);

        centerWrapper.add(card, BorderLayout.NORTH);
        content.add(centerWrapper, BorderLayout.CENTER);

        // FOOTER (bottoni)
        JPanel buttons = new JPanel();
        buttons.setBackground(DARK_BG);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnPaga = creaBottoneArancione("Paga e attiva abbonamento");
        btnAnnulla = creaBottoneSoloBordo("Annulla");

        buttons.add(btnPaga);
        buttons.add(Box.createHorizontalStrut(15));
        buttons.add(btnAnnulla);

        content.add(buttons, BorderLayout.SOUTH);

        // Listener
        btnPaga.addActionListener(e -> {
            if (action != null) {
                String metodo = (String) comboMetodo.getSelectedItem();
                action.handlePaga(metodo);
            }
        });

        btnAnnulla.addActionListener(e -> {
            if (action != null) action.handleAnnulla();
        });
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

    // ===== Contract =====
    @Override
    public void mostraMessaggioInfo(String msg) {
        ThemedDialog.showMessage(this, "Info", msg, false);
    }

    @Override
    public void mostraMessaggioErrore(String msg) {
        ThemedDialog.showMessage(this, "Errore", msg, true);
    }

    @Override
    public Cliente getCliente() { return cliente; }

    @Override
    public Abbonamento getAbbonamento() { return abbonamento; }

    @Override
    public Component asComponent() { return this; }
}