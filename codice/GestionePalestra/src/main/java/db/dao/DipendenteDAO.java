package db.dao;

import db.GestioneDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DipendenteDAO {

    public static class DipendenteInfo {
        public final int id;
        public final String nomeCompleto;

        public DipendenteInfo(int id, String nomeCompleto) {
            this.id = id;
            this.nomeCompleto = nomeCompleto;
        }
    }

    /** Ritorna l’elenco dei dipendenti per ruolo (per popolare la combo) */
    public static List<DipendenteInfo> findByRuolo(String ruolo) throws Exception {
        String sql = "SELECT ID_DIPENDENTE, NOME, COGNOME " +
                     "FROM DIPENDENTE WHERE RUOLO = ? ORDER BY COGNOME, NOME";

        List<DipendenteInfo> lista = new ArrayList<>();

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ruolo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID_DIPENDENTE");
                    String nome = rs.getString("NOME");
                    String cognome = rs.getString("COGNOME");
                    lista.add(new DipendenteInfo(id, nome + " " + cognome));
                }
            }
        }

        return lista;
    }

    /**
     * Restituisce una descrizione testuale del dipendente, usando
     * i dati della tabella DIPENDENTE e della relativa sottoclasse
     * (PERSONAL_TRAINER, NUTRIZIONISTA, ISTRUTTORE_CORSO).
     */
    public static String getDescrizioneDipendente(int idDipendente) throws Exception {

        String sql =
                "SELECT d.NOME, d.COGNOME, d.RUOLO, d.ORARIO_DISP, " +
                "       pt.ANNI_ESPERIENZA, pt.CERTIFICATI, pt.PARTITA_IVA, " +
                "       n.PARCELLA, " +
                "       ic.TIPO_CORSO_INSEGNATO " +
                "FROM DIPENDENTE d " +
                "LEFT JOIN PERSONAL_TRAINER pt ON d.ID_DIPENDENTE = pt.ID_DIPENDENTE " +
                "LEFT JOIN NUTRIZIONISTA n ON d.ID_DIPENDENTE = n.ID_DIPENDENTE " +
                "LEFT JOIN ISTRUTTORE_CORSO ic ON d.ID_DIPENDENTE = ic.ID_DIPENDENTE " +
                "WHERE d.ID_DIPENDENTE = ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDipendente);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return "Dettagli non disponibili per questo dipendente.";
                }

                String nome       = rs.getString("NOME");
                String cognome    = rs.getString("COGNOME");
                String ruolo      = rs.getString("RUOLO");
                String orarioDisp = rs.getString("ORARIO_DISP");

                StringBuilder sb = new StringBuilder();
                sb.append(nome).append(" ").append(cognome).append("\n");
                sb.append("Ruolo: ").append(ruolo).append("\n");
                sb.append("Disponibilità: ").append(orarioDisp).append("\n");

                if ("PERSONAL_TRAINER".equalsIgnoreCase(ruolo)) {
                    int anniEsp       = rs.getInt("ANNI_ESPERIENZA");
                    String cert       = rs.getString("CERTIFICATI");
                    String pIva       = rs.getString("PARTITA_IVA");

                    if (anniEsp > 0) {
                        sb.append("Anni di esperienza: ").append(anniEsp).append("\n");
                    }
                    if (cert != null && !cert.isEmpty()) {
                        sb.append("Certificazioni: ").append(cert).append("\n");
                    }
                    if (pIva != null && !pIva.isEmpty()) {
                        sb.append("Partita IVA: ").append(pIva).append("\n");
                    }

                } else if ("NUTRIZIONISTA".equalsIgnoreCase(ruolo)) {
                    String parcella = rs.getString("PARCELLA");
                    if (parcella != null && !parcella.isEmpty()) {
                        sb.append("Parcella indicativa: ").append(parcella).append("\n");
                    }

                } else if ("ISTRUTTORE_CORSO".equalsIgnoreCase(ruolo)) {
                    String tipoCorso = rs.getString("TIPO_CORSO_INSEGNATO");
                    if (tipoCorso != null && !tipoCorso.isEmpty()) {
                        sb.append("Corso insegnato: ").append(tipoCorso).append("\n");
                    }
                }

                return sb.toString();
            }
        }
    }
}
