package db.dao.corso;

import db.dao.UtilsDAO;
import model.corsi.LezioneInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LezioneDAO {

    private static final String SQL_PROGRAMMAZIONE_CORSO =
            "SELECT DATA_LEZIONE, ORA_LEZIONE " +
            "FROM LEZIONE_CORSO " +
            "WHERE ID_CORSO = ? " +
            "ORDER BY DATA_LEZIONE, ORA_LEZIONE";

    private static final String SQL_LEZIONI_PER_CORSO =
            "SELECT L.ID_LEZIONE, L.ID_CORSO, L.DATA_LEZIONE, L.ORA_LEZIONE, " +
            "       L.POSTI_TOTALI, L.POSTI_PRENOTATI, " +
            "       C.DURATA_MINUTI, D.NOME, D.COGNOME " +
            "FROM LEZIONE_CORSO L " +
            "JOIN CORSO C ON L.ID_CORSO = C.ID_CORSO " +
            "JOIN DIPENDENTE D ON L.ID_ISTRUTTORE = D.ID_DIPENDENTE " +
            "WHERE L.ID_CORSO = ? " +
            "ORDER BY L.DATA_LEZIONE, L.ORA_LEZIONE";

    private static final String SQL_SELECT_TUTTE_LEZIONI =
            "SELECT ID_LEZIONE, DATA_LEZIONE FROM LEZIONE_CORSO";

    private static final String SQL_UPDATE_DATA_LEZIONE =
            "UPDATE LEZIONE_CORSO SET DATA_LEZIONE = ? WHERE ID_LEZIONE = ?";

    // ===================== UTIL PER PROGRAMMAZIONE =====================

    private static String abbreviazioneGiorno(DayOfWeek d) {
        switch (d) {
            case MONDAY:    return "Lun";
            case TUESDAY:   return "Mar";
            case WEDNESDAY: return "Mer";
            case THURSDAY:  return "Gio";
            case FRIDAY:    return "Ven";
            case SATURDAY:  return "Sab";
            case SUNDAY:    return "Dom";
            default:        return d.toString();
        }
    }

    public static String buildDescrizioneProgrammazioneCorso(int idCorso) throws Exception {
        Map<LocalTime, List<String>> byOra =
        		UtilsDAO.withConnection(conn -> caricaMappaProgrammazione(conn, idCorso));
        return formattaProgrammazione(byOra);
    }

    private static Map<LocalTime, List<String>> caricaMappaProgrammazione(Connection conn,
                                                                          int idCorso) throws SQLException {
        Map<LocalTime, List<String>> byOra = new LinkedHashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_PROGRAMMAZIONE_CORSO)) {
            ps.setInt(1, idCorso);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate data = rs.getDate("DATA_LEZIONE").toLocalDate();
                    LocalTime ora  = rs.getTime("ORA_LEZIONE").toLocalTime();
                    DayOfWeek dow  = data.getDayOfWeek();
                    String giorno  = abbreviazioneGiorno(dow);

                    byOra.computeIfAbsent(ora, k -> new ArrayList<>()).add(giorno);
                }
            }
        }

        return byOra;
    }

    private static String formattaProgrammazione(Map<LocalTime, List<String>> byOra) {
        if (byOra.isEmpty()) {
            return "Nessuna lezione programmata.";
        }

        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;

        for (Map.Entry<LocalTime, List<String>> entry : byOra.entrySet()) {
            if (!firstTime) {
                sb.append(" / ");
            }
            firstTime = false;

            LocalTime ora = entry.getKey();
            List<String> giorni = entry.getValue();

            Set<String> unique = new LinkedHashSet<>(giorni);
            String giorniStr = String.join("-", unique);

            String oraStr = ora.toString();
            if (oraStr.length() > 5) {
                oraStr = oraStr.substring(0, 5);
            }

            sb.append(giorniStr).append(" ").append(oraStr);
        }

        return sb.toString();
    }

    // ===================== LEZIONI =====================

    public static List<LezioneInfo> getLezioniPerCorso(int idCorso) throws Exception {
        LocalDate oggi = LocalDate.now();
        return UtilsDAO.withConnection(conn -> getLezioniPerCorso(conn, idCorso, oggi));
    }

    private static List<LezioneInfo> getLezioniPerCorso(Connection conn,
                                                        int idCorso,
                                                        LocalDate oggi) throws SQLException {

        List<LezioneInfo> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_LEZIONI_PER_CORSO)) {
            ps.setInt(1, idCorso);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LezioneInfo info = creaLezioneInfoDaResultSet(idCorso, rs, oggi);
                    result.add(info);
                }
            }
        }

        return result;
    }

    private static LezioneInfo creaLezioneInfoDaResultSet(int idCorso,
                                                          ResultSet rs,
                                                          LocalDate oggi) throws SQLException {

        int idLezione = rs.getInt("ID_LEZIONE");
        LocalDate data = rs.getDate("DATA_LEZIONE").toLocalDate();
        LocalTime ora  = rs.getTime("ORA_LEZIONE").toLocalTime();
        int postiTot   = rs.getInt("POSTI_TOTALI");
        int postiPren  = rs.getInt("POSTI_PRENOTATI");
        int durata     = rs.getInt("DURATA_MINUTI");
        String nomeIstr = rs.getString("NOME") + " " + rs.getString("COGNOME");

        while (!data.isAfter(oggi)) {
            data = data.plusWeeks(1);
        }

        return new LezioneInfo(
                idLezione, idCorso, data, ora,
                durata, postiTot, postiPren, nomeIstr
        );
    }

    // ===================== AGGIORNAMENTO DATE LEZIONI =====================

    public static void aggiornaDateLezioniAllaSettimanaCorrente() throws Exception {
        LocalDate oggi = LocalDate.now();
        UtilsDAO.withConnection(conn -> {
            aggiornaDateLezioniAllaSettimanaCorrente(conn, oggi);
            return null;
        });
    }

    private static void aggiornaDateLezioniAllaSettimanaCorrente(Connection conn,
                                                                  LocalDate oggi) throws SQLException {
        try (PreparedStatement psSel = conn.prepareStatement(SQL_SELECT_TUTTE_LEZIONI);
             ResultSet rs = psSel.executeQuery();
             PreparedStatement psUpd = conn.prepareStatement(SQL_UPDATE_DATA_LEZIONE)) {

            aggiornaDateLezioniFromResultSet(oggi, rs, psUpd);
            psUpd.executeBatch();
        }
    }

    private static void aggiornaDateLezioniFromResultSet(LocalDate oggi,
                                                         ResultSet rs,
                                                         PreparedStatement psUpd) throws SQLException {

        while (rs.next()) {
            int idLez = rs.getInt("ID_LEZIONE");
            LocalDate dataOriginale = rs.getDate("DATA_LEZIONE").toLocalDate();
            LocalDate dataNuova = calcolaDataAggiornata(dataOriginale, oggi);

            if (!dataNuova.equals(dataOriginale)) {
                psUpd.setDate(1, Date.valueOf(dataNuova));
                psUpd.setInt(2, idLez);
                psUpd.addBatch();
            }
        }
    }

    private static LocalDate calcolaDataAggiornata(LocalDate dataOriginale, LocalDate oggi) {
        LocalDate dataNuova = dataOriginale;
        while (dataNuova.isBefore(oggi)) {
            dataNuova = dataNuova.plusWeeks(1);
        }
        return dataNuova;
    }
}
