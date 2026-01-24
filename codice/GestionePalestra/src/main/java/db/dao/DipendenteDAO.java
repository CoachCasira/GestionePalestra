package db.dao;

import db.GestioneDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // ==========================================================
    //  NUOVO: VALIDAZIONE DISPONIBILITÀ DIPENDENTE (ORARIO_DISP)
    // ==========================================================

    /**
     * Verifica se un dipendente è disponibile nella fascia richiesta, in base al campo ORARIO_DISP.
     *
     * Supporta formati tipo:
     * - "Sab 9:00-12:00"
     * - "Lun-Ven 9:00-13:00"
     * - "Mar-Gio 10:00-16:00"
     * - "Lun-Mer 15:00-19:00"
     *
     * Se ORARIO_DISP non contiene una fascia oraria (es. "Corsi serali"), il metodo ritorna true
     * per non alterare il comportamento pre-esistente (gestione non vincolata a orari).
     */
    public static boolean isDisponibile(int idDipendente,
                                        LocalDate data,
                                        LocalTime oraInizio,
                                        int durataMinuti) throws Exception {

        String orarioDisp = getOrarioDisponibilita(idDipendente);

        // Nessuna info -> non blocco
        if (orarioDisp == null || orarioDisp.trim().isEmpty()) {
            return true;
        }

        // Disponibilità non oraria (es. "Corsi serali") -> non blocco
        if (!containsTimeRange(orarioDisp)) {
            return true;
        }

        LocalTime oraFine = oraInizio.plusMinutes(durataMinuti);
        DayOfWeek giorno = data.getDayOfWeek();

        // Supporta più segmenti separati da ; o ,
        String[] segmenti = orarioDisp.split("[;,]");
        for (String seg : segmenti) {
            if (matchesSegment(seg.trim(), giorno, oraInizio, oraFine)) {
                return true;
            }
        }
        return false;
    }

    private static String getOrarioDisponibilita(int idDipendente) throws Exception {
        String sql = "SELECT ORARIO_DISP FROM DIPENDENTE WHERE ID_DIPENDENTE = ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDipendente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ORARIO_DISP");
                }
            }
        }
        return null;
    }

    private static final Pattern TIME_RANGE =
            Pattern.compile("(\\d{1,2}:\\d{2})\\s*-\\s*(\\d{1,2}:\\d{2})");

    private static boolean containsTimeRange(String s) {
        return TIME_RANGE.matcher(s).find();
    }

    private static boolean matchesSegment(String segment,
                                          DayOfWeek giorno,
                                          LocalTime oraInizio,
                                          LocalTime oraFine) {

        if (segment == null || segment.isEmpty()) return false;

        // Il token dei giorni è il primo elemento (es. "Sab" o "Lun-Ven")
        String[] parts = segment.split("\\s+");
        if (parts.length < 2) return false;

        String giorniToken = parts[0].trim();

        Matcher m = TIME_RANGE.matcher(segment);
        if (!m.find()) return false;

        LocalTime dispStart;
        LocalTime dispEnd;
        try {
            dispStart = LocalTime.parse(m.group(1));
            dispEnd   = LocalTime.parse(m.group(2));
        } catch (Exception e) {
            return false;
        }

        if (!dayMatches(giorniToken, giorno)) {
            return false;
        }

        // Intervallo richiesto [oraInizio, oraFine) deve stare dentro [dispStart, dispEnd]
        return !oraInizio.isBefore(dispStart) && !oraFine.isAfter(dispEnd);
    }

    private static boolean dayMatches(String token, DayOfWeek day) {
        token = token.trim();

        if (!token.contains("-")) {
            DayOfWeek single = parseItDay(token);
            return single != null && single == day;
        }

        String[] ab = token.split("-");
        if (ab.length != 2) return false;

        DayOfWeek start = parseItDay(ab[0].trim());
        DayOfWeek end   = parseItDay(ab[1].trim());
        if (start == null || end == null) return false;

        int s = start.getValue(); // MON=1 ... SUN=7
        int e = end.getValue();
        int d = day.getValue();

        // Range normale (es. Lun-Ven)
        if (s <= e) {
            return d >= s && d <= e;
        }

        // Range che attraversa la domenica (robustezza, es. Ven-Lun)
        return d >= s || d <= e;
    }

    private static DayOfWeek parseItDay(String abbr) {
        String a = abbr.toLowerCase();

        // accettiamo sia abbreviazioni che forme estese (robustezza)
        if (a.startsWith("lun")) return DayOfWeek.MONDAY;
        if (a.startsWith("mar")) return DayOfWeek.TUESDAY;
        if (a.startsWith("mer")) return DayOfWeek.WEDNESDAY;
        if (a.startsWith("gio")) return DayOfWeek.THURSDAY;
        if (a.startsWith("ven")) return DayOfWeek.FRIDAY;
        if (a.startsWith("sab")) return DayOfWeek.SATURDAY;
        if (a.startsWith("dom")) return DayOfWeek.SUNDAY;
        return null;
    }
}
