package view.dialog;

import javax.swing.*;

import java.awt.*;

/**
 * Dialog / messaggi con il tema scuro-arancione di GestionePalestra.
 */
public class ThemedDialog {

    // Palette condivisa
    private static final Color DARK_BG   = new Color(20, 20, 20);
    private static final Color ORANGE    = new Color(255, 140, 0);
    private static final Color ORANGE_HO = new Color(255, 170, 40);

    // larghezza "tipica telefono" per il testo del messaggio (più stretta)
    private static final int MESSAGE_WIDTH_PX = 280;

    // ===============================
    //  MESSAGGIO SEMPLICE (OK)
    // ===============================
    public static void showMessage(Window owner, String titolo, String msg, boolean errore) {
        final JDialog dialog = new JDialog(
                owner,
                titolo,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);
        // padding un filo più piccolo
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitolo = new JLabel(titolo);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitolo.setForeground(errore ? Color.RED : ORANGE);

        // testo con larghezza fissa → la finestra non si allarga oltre
        String htmlMsg =
                "<html><body style='width:" + MESSAGE_WIDTH_PX + "px;"
                        + "color:white;font-family:SansSerif;font-size:13px;'>"
                        + msg.replace("\n", "<br>")
                        + "</body></html>";
        JLabel lblMsg = new JLabel(htmlMsg);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(DARK_BG);
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(lblTitolo);
        top.add(Box.createVerticalStrut(10));
        top.add(lblMsg);

        JButton btnOk = new JButton("OK");
        btnOk.setBackground(ORANGE);
        btnOk.setForeground(Color.BLACK);
        btnOk.setFocusPainted(false);
        // padding orizzontale più contenuto
        btnOk.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        btnOk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOk.setOpaque(true);                 // importante su macOS
        btnOk.setContentAreaFilled(true);
        btnOk.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btnOk.setBackground(ORANGE_HO); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btnOk.setBackground(ORANGE); }
        });
        btnOk.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel();
        bottom.setBackground(DARK_BG);
        bottom.add(btnOk);

        panel.add(top, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);   // centrato sulla finestra padre
        dialog.setVisible(true);
    }

    // ===============================
    //  CONFERMA (Sì / No)
    // ===============================
    public static boolean showConfirm(Window owner, String titolo, String msg) {
        final JDialog dialog = new JDialog(
                owner,
                titolo,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitolo = new JLabel(titolo);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitolo.setForeground(ORANGE);

        String htmlMsg =
                "<html><body style='width:" + MESSAGE_WIDTH_PX + "px;"
                        + "color:white;font-family:SansSerif;font-size:13px;'>"
                        + msg.replace("\n", "<br>")
                        + "</body></html>";
        JLabel lblMsg = new JLabel(htmlMsg);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(DARK_BG);
        lblTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(lblTitolo);
        top.add(Box.createVerticalStrut(10));
        top.add(lblMsg);

        // risultato da restituire
        final boolean[] result = new boolean[1];
        result[0] = false;

        JButton btnNo = new JButton("No");
        styleSecondaryButton(btnNo);
        btnNo.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        JButton btnSi = new JButton("Sì");
        stylePrimaryButton(btnSi);
        btnSi.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(DARK_BG);
        bottom.add(btnNo);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(btnSi);

        panel.add(top, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return result[0];
    }

    // ===============================
    //  Stile bottoni
    // ===============================
    private static void stylePrimaryButton(JButton b) {
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

    private static void styleSecondaryButton(JButton b) {
        b.setBackground(DARK_BG);
        b.setForeground(ORANGE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(ORANGE));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(40, 40, 40)); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { b.setBackground(DARK_BG); }
        });
    }
}
