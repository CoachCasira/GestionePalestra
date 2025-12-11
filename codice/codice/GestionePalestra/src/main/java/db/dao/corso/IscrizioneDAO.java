package db.dao.corso;

import db.dao.UtilsDAO;
import model.corsi.IscrizioneInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class IscrizioneDAO {

    private static final String SQL_CONFLITTO_CLIENTE =
            "SELECT L.DATA_LEZIONE, L.ORA_LEZIONE, C.DURATA_MINUTI " +
            "FROM ISCRIZIONE_CORSO I " +
            "JOIN LEZIONE_CORSO L ON I.ID_LEZIONE = L.ID_LEZIONE " +
            "JOIN CORSO C ON L.ID_CORSO = C.ID_CORSO " +
            "WHERE I.ID_CLIENTE = ? AND L.DATA_LEZIONE = ?";

    private static final String SQL_LEZIONE_POSTI =
            "SELECT POSTI_TOTALI, POSTI_PRENOTATI FROM LEZIONE_CORSO WHERE ID_LEZIONE = ?";

    private static final String SQL_LEZIONE_POSTI_FOR_UPDATE =
            "SELECT POSTI_TOTALI, POSTI_PRENOTATI FROM LEZIONE_CORSO " +
            "WHERE ID_LEZIONE = ? FOR UPDATE";

    private static final String SQL_UPDATE_POSTI_INC =
            "UPDATE LEZIONE_CORSO SET POSTI_PRENOTATI = POSTI_PRENOTATI + 1 WHERE ID_LEZIONE = ?";

    private static final String SQL_UPDATE_POSTI_DEC =
            "UPDATE LEZIONE_CORSO SET POSTI_PRENOTATI = POSTI_PRENOTATI - 1 WHERE ID_LEZIONE = ?";

    private static final String SQL_INSERT_ISCRIZIONE =
            "INSERT INTO ISCRIZIONE_CORSO (ID_CLIENTE, ID_LEZIONE) VALUES (?, ?)";

    private static final String SQL_DELETE_ISCRIZIONE =
            "DELETE FROM ISCRIZIONE_CORSO WHERE ID_CLIENTE = ? AND ID_LEZIONE = ?";

    private static final String SQL_ISCRIZIONI_FUTURE_CLIENTE =
            "SELECT L.ID_LEZIONE, L.ID_CORSO, L.DATA_LEZIONE, L.ORA_LEZIONE, " +
            "       C.NOME AS NOME_CORSO, C.DURATA_MINUTI, " +
            "       D.NOME, D.COGNOME " +
            "FROM ISCRIZIONE_CORSO I " +
            "JOIN LEZIONE_CORSO L ON I.ID_LEZIONE = L.ID_LEZIONE " +
            "JOIN CORSO C ON L.ID_CORSO = C.ID_CORSO " +
            "JOIN DIPENDENTE D ON L.ID_ISTRUTTORE = D.ID_DIPENDENTE " +
            "WHERE I.ID_CLIENTE = ? AND L.DATA_LEZIONE >= ? " +
            "ORDER BY L.DATA_LEZIONE, L.ORA_LEZIONE";

    private static final String SQL_DETTAGLIO_ISCRIZIONI_CLIENTE =
            "SELECT C.NOME, C.DESCRIZIONE, C.DURATA_MINUTI, " +
            "       L.DATA_LEZIONE, L.ORA_LEZIONE, " +
            "       L.POSTI_TOTALI, L.POSTI_PRENOTATI, " +
            "       D.NOME AS NOME_IST, D.COGNOME AS COGNOME_IST " +
            "FROM ISCRIZIONE_CORSO I " +
            "JOIN LEZIONE_CORSO L ON I.ID_LEZIONE = L.ID_LEZIONE " +
            "JOIN CORSO C ON L.ID_CORSO = C.ID_CORSO " +
            "JOIN DIPENDENTE D ON L.ID_ISTRUTTORE = D.ID_DIPENDENTE " +
            "WHERE I.ID_CLIENTE = ? " +
            "ORDER BY L.DATA_LEZIONE, L.ORA_LEZIONE";

    private static final String SQL_COUNT_ISCRIZIONI_FUTURE_CLIENTE =
            "SELECT COUNT(*) FROM ISCRIZIONE_CORSO I " +
            "JOIN LEZIONE_CORSO L ON I.ID_LEZIONE = L.ID_LEZIONE " +
            "WHERE I.ID_CLIENTE = ? AND L.DATA_LEZIONE >= ?";

    // ===================== CONFLITTI =====================

    public static boolean esisteConflittoPerCliente(int idCliente,
                                                    LocalDate dataNuova,
                                                    LocalTime oraNuova,
                                                    int durataNuovaMin) throws Exception {

        LocalDateTime inizioNuovo = LocalDateTime.of(dataNuova, oraNuova);
        LocalDateTime fineNuovo   = inizioNuovo.plusMinutes(durataNuovaMin);

        return UtilsDAO.withConnection(conn ->
                esisteConflittoPerCliente(conn, idCliente, dataNuova, inizioNuovo, fineNuovo));
    }

    private static boolean esisteConflittoPerCliente(Connection conn,
                                                     int idCliente,
                                                     LocalDate dataNuova,
                                                     LocalDateTime inizioNuovo,
                                                     LocalDateTime fineNuovo) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(SQL_CONFLITTO_CLIENTE)) {
            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(dataNuova));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (esisteOverlapConIscrizioneEsistente(rs, inizioNuovo, fineNuovo)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean esisteOverlapConIscrizioneEsistente(ResultSet rs,
                                                               LocalDateTime inizioNuovo,
                                                               LocalDateTime fineNuovo) throws SQLException {

        LocalDate dataEsistente = rs.getDate("DATA_LEZIONE").toLocalDate();
        LocalTime oraEsistente  = rs.getTime("ORA_LEZIONE").toLocalTime();
        int durataEsistente     = rs.getInt("DURATA_MINUTI");

        LocalDateTime inizioEsistente = LocalDateTime.of(dataEsistente, oraEsistente);
        LocalDateTime fineEsistente   = inizioEsistente.plusMinutes(durataEsistente);

        return intervalliSiSovrappongono(inizioNuovo, fineNuovo, inizioEsistente, fineEsistente);
    }

    private static boolean intervalliSiSovrappongono(LocalDateTime inizio1,
                                                     LocalDateTime fine1,
                                                     LocalDateTime inizio2,
                                                     LocalDateTime fine2) {
        return !inizio1.isAfter(fine2) && !fine1.isBefore(inizio2);
    }

    // ===================== POSTI DISPONIBILI =====================

    public static boolean haPostiDisponibili(int idLezione) throws Exception {
        return UtilsDAO.withConnection(conn -> haPostiDisponibili(conn, idLezione));
    }

    private static boolean haPostiDisponibili(Connection conn,
                                              int idLezione) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_LEZIONE_POSTI)) {
            ps.setInt(1, idLezione);
            try (ResultSet rs = ps.executeQuery()) {
                return verificaPostiDisponibili(rs);
            }
        }
    }

    private static boolean verificaPostiDisponibili(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return false;
        }
        int tot = rs.getInt("POSTI_TOTALI");
        int pren = rs.getInt("POSTI_PRENOTATI");
        return pren < tot;
    }

    // ===================== ISCRIZIONE =====================

    public static void iscriviClienteALezione(int idCliente, int idLezione) throws Exception {
    	UtilsDAO.withConnection(conn -> {
            iscriviClienteALezione(conn, idCliente, idLezione);
            return null;
        });
    }

    private static void iscriviClienteALezione(Connection conn,
                                               int idCliente,
                                               int idLezione) throws Exception {
        conn.setAutoCommit(false);
        try {
            verificaPostiDisponibiliPerTransazione(conn, idLezione);
            incrementaPostiPrenotati(conn, idLezione);
            inserisciIscrizione(conn, idCliente, idLezione);
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static void verificaPostiDisponibiliPerTransazione(Connection conn,
                                                               int idLezione) throws Exception {
        try (PreparedStatement psCheck = conn.prepareStatement(SQL_LEZIONE_POSTI_FOR_UPDATE)) {
            psCheck.setInt(1, idLezione);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Lezione inesistente.");
                }
                int tot  = rs.getInt("POSTI_TOTALI");
                int pren = rs.getInt("POSTI_PRENOTATI");
                if (pren >= tot) {
                    throw new Exception("Nessun posto disponibile per questa lezione.");
                }
            }
        }
    }

    private static void incrementaPostiPrenotati(Connection conn,
                                                 int idLezione) throws SQLException {
        try (PreparedStatement psUpd = conn.prepareStatement(SQL_UPDATE_POSTI_INC)) {
            psUpd.setInt(1, idLezione);
            psUpd.executeUpdate();
        }
    }

    private static void inserisciIscrizione(Connection conn,
                                            int idCliente,
                                            int idLezione) throws SQLException {
        try (PreparedStatement psIns = conn.prepareStatement(SQL_INSERT_ISCRIZIONE)) {
            psIns.setInt(1, idCliente);
            psIns.setInt(2, idLezione);
            psIns.executeUpdate();
        }
    }

    // ===================== DISISCRIZIONE =====================

    public static void disiscriviClienteDaLezione(int idCliente, int idLezione) throws Exception {
    	UtilsDAO.withConnection(conn -> {
            disiscriviClienteDaLezione(conn, idCliente, idLezione);
            return null;
        });
    }

    private static void disiscriviClienteDaLezione(Connection conn,
                                                   int idCliente,
                                                   int idLezione) throws Exception {
        conn.setAutoCommit(false);
        try {
            verificaIscrizioneEsistente(conn, idLezione);
            decrementaPostiPrenotati(conn, idLezione);
            eliminaIscrizione(conn, idCliente, idLezione);
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static void verificaIscrizioneEsistente(Connection conn,
                                                    int idLezione) throws Exception {
        try (PreparedStatement psCheck = conn.prepareStatement(SQL_LEZIONE_POSTI_FOR_UPDATE)) {
            psCheck.setInt(1, idLezione);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Lezione inesistente.");
                }
                int pren = rs.getInt("POSTI_PRENOTATI");
                if (pren <= 0) {
                    throw new Exception("Nessuna iscrizione da rimuovere per questa lezione.");
                }
            }
        }
    }

    private static void decrementaPostiPrenotati(Connection conn,
                                                 int idLezione) throws SQLException {
        try (PreparedStatement psUpd = conn.prepareStatement(SQL_UPDATE_POSTI_DEC)) {
            psUpd.setInt(1, idLezione);
            psUpd.executeUpdate();
        }
    }

    private static void eliminaIscrizione(Connection conn,
                                          int idCliente,
                                          int idLezione) throws SQLException {
        try (PreparedStatement psDel = conn.prepareStatement(SQL_DELETE_ISCRIZIONE)) {
            psDel.setInt(1, idCliente);
            psDel.setInt(2, idLezione);
            psDel.executeUpdate();
        }
    }

    // ===================== ISCRIZIONI FUTURE / DETTAGLIO =====================

    public static List<IscrizioneInfo> getIscrizioniFuturePerCliente(int idCliente) throws Exception {
        return UtilsDAO.withConnection(conn -> getIscrizioniFuturePerCliente(conn, idCliente));
    }

    private static List<IscrizioneInfo> getIscrizioniFuturePerCliente(Connection conn,
                                                                      int idCliente) throws SQLException {
        List<IscrizioneInfo> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_ISCRIZIONI_FUTURE_CLIENTE)) {
            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(creaIscrizioneInfoDaResultSet(rs));
                }
            }
        }

        return result;
    }

    private static IscrizioneInfo creaIscrizioneInfoDaResultSet(ResultSet rs) throws SQLException {
        int idLezione = rs.getInt("ID_LEZIONE");
        int idCorso   = rs.getInt("ID_CORSO");
        LocalDate data = rs.getDate("DATA_LEZIONE").toLocalDate();
        LocalTime ora  = rs.getTime("ORA_LEZIONE").toLocalTime();
        int durata = rs.getInt("DURATA_MINUTI");
        String nomeCorso = rs.getString("NOME_CORSO");
        String nomeIstr = rs.getString("NOME") + " " + rs.getString("COGNOME");

        return new IscrizioneInfo(
                idLezione, idCorso, nomeCorso,
                data, ora, durata, nomeIstr
        );
    }

    public static String buildDettaglioIscrizioniPerCliente(int idCliente) throws Exception {
        return UtilsDAO.withConnection(conn -> buildDettaglioIscrizioniPerCliente(conn, idCliente));
    }

    private static String buildDettaglioIscrizioniPerCliente(Connection conn,
                                                             int idCliente) throws SQLException {
        List<String> future = new ArrayList<>();
        List<String> past   = new ArrayList<>();
        LocalDateTime now   = LocalDateTime.now();

        caricaIscrizioniPerCliente(conn, idCliente, future, past, now);

        if (future.isEmpty() && past.isEmpty()) {
            return "Non hai ancora nessuna iscrizione ai corsi.\n";
        }

        return formattaDettaglioIscrizioni(future, past);
    }

    private static void caricaIscrizioniPerCliente(Connection conn,
                                                   int idCliente,
                                                   List<String> future,
                                                   List<String> past,
                                                   LocalDateTime now) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(SQL_DETTAGLIO_ISCRIZIONI_CLIENTE)) {
            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String entry = formattaRigaIscrizione(rs);
                    LocalDateTime inizio = ottieniInizioLezione(rs);

                    if (inizio.isBefore(now)) {
                        past.add(entry);
                    } else {
                        future.add(entry);
                    }
                }
            }
        }
    }

    private static LocalDateTime ottieniInizioLezione(ResultSet rs) throws SQLException {
        LocalDate data = rs.getDate("DATA_LEZIONE").toLocalDate();
        LocalTime ora  = rs.getTime("ORA_LEZIONE").toLocalTime();
        return LocalDateTime.of(data, ora);
    }

    private static String formattaRigaIscrizione(ResultSet rs) throws SQLException {
        String nomeCorso = rs.getString("NOME");
        String descr = rs.getString("DESCRIZIONE");
        int durata = rs.getInt("DURATA_MINUTI");
        LocalDate data = rs.getDate("DATA_LEZIONE").toLocalDate();
        LocalTime ora  = rs.getTime("ORA_LEZIONE").toLocalTime();
        int tot  = rs.getInt("POSTI_TOTALI");
        int pren = rs.getInt("POSTI_PRENOTATI");
        String nomeIstr = rs.getString("NOME_IST") + " " + rs.getString("COGNOME_IST");

        StringBuilder sb = new StringBuilder();
        sb.append("- ").append(data).append(" ore ").append(ora)
          .append("\nCorso: ").append(nomeCorso)
          .append("\nIstruttore: ").append(nomeIstr)
          .append("\nDurata: ").append(durata).append(" minuti")
          .append("\nPosti: ").append(pren).append("/").append(tot)
          .append("\nDescrizione: ").append(descr)
          .append("\n\n");
        return sb.toString();
    }

    private static String formattaDettaglioIscrizioni(List<String> future,
                                                      List<String> past) {
        StringBuilder sb = new StringBuilder();

        if (!future.isEmpty()) {
            sb.append("Corsi futuri:\n\n");
            future.forEach(sb::append);
        }

        if (!past.isEmpty()) {
            if (!future.isEmpty()) {
                sb.append("\n");
            }
            sb.append("Corsi già svolti:\n\n");
            past.forEach(sb::append);
        }

        return sb.toString();
    }

    // ===================== CHECK DI ESISTENZA =====================

    public static boolean esistonoIscrizioniFuturePerCliente(int idCliente) throws Exception {
        return UtilsDAO.withConnection(conn ->
                esistonoIscrizioniFuturePerCliente(conn, idCliente));
    }

    private static boolean esistonoIscrizioniFuturePerCliente(Connection conn,
                                                              int idCliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_ISCRIZIONI_FUTURE_CLIENTE)) {

            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
