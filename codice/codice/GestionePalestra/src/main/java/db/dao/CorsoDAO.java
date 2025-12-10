package db.dao;

import db.GestioneDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CorsoDAO {

    // ===================== DTO CORSI =====================
    public static class CorsoInfo {
        public final int idCorso;
        public final String nome;
        public final String descrizione;
        public final int durataMinuti;

        public CorsoInfo(int idCorso, String nome, String descrizione, int durataMinuti) {
            this.idCorso = idCorso;
            this.nome = nome;
            this.descrizione = descrizione;
            this.durataMinuti = durataMinuti;
        }
    }

    // ===================== DTO LEZIONI =====================
    public static class LezioneInfo {
        public final int idLezione;
        public final int idCorso;
        public final LocalDate data;
        public final LocalTime ora;
        public final int durataMinuti;
        public final int postiTotali;
        public final int postiPrenotati;
        public final String nomeIstruttore;  // "Nome Cognome"

        public LezioneInfo(int idLezione,
                           int idCorso,
                           LocalDate data,
                           LocalTime ora,
                           int durataMinuti,
                           int postiTotali,
                           int postiPrenotati,
                           String nomeIstruttore) {
            this.idLezione = idLezione;
            this.idCorso = idCorso;
            this.data = data;
            this.ora = ora;
            this.durataMinuti = durataMinuti;
            this.postiTotali = postiTotali;
            this.postiPrenotati = postiPrenotati;
            this.nomeIstruttore = nomeIstruttore;
        }

        public int postiDisponibili() {
            return postiTotali - postiPrenotati;
        }

        public LocalDateTime getInizio() {
            return LocalDateTime.of(data, ora);
        }

        public LocalDateTime getFine() {
            return getInizio().plusMinutes(durataMinuti);
        }
    }

    // ===================== DTO ISCRIZIONI PER DISDETTA =====================
    public static class IscrizioneInfo {
        public final int idLezione;
        public final int idCorso;
        public final String nomeCorso;
        public final LocalDate data;
        public final LocalTime ora;
        public final int durataMinuti;
        public final String nomeIstruttore;

        public IscrizioneInfo(int idLezione,
                              int idCorso,
                              String nomeCorso,
                              LocalDate data,
                              LocalTime ora,
                              int durataMinuti,
                              String nomeIstruttore) {
            this.idLezione = idLezione;
            this.idCorso = idCorso;
            this.nomeCorso = nomeCorso;
            this.data = data;
            this.ora = ora;
            this.durataMinuti = durataMinuti;
            this.nomeIstruttore = nomeIstruttore;
        }

        public LocalDateTime getInizio() {
            return LocalDateTime.of(data, ora);
        }
    }

    // ===================== COSTANTI SQL =====================

    private static final String SQL_PROGRAMMAZIONE_CORSO =
            "SELECT DATA_LEZIONE, ORA_LEZIONE " +
            "FROM LEZIONE_CORSO " +
            "WHERE ID_CORSO = ? " +
            "ORDER BY DATA_LEZIONE, ORA_LEZIONE";

    private static final String SQL_TUTTI_I_CORSI =
            "SELECT ID_CORSO, NOME, DESCRIZIONE, DURATA_MINUTI " +
            "FROM CORSO ORDER BY NOME";

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

    private static final String SQL_COUNT_CORSI =
            "SELECT COUNT(*) FROM CORSO";

    private static final String SQL_COUNT_ISCRIZIONI_FUTURE_CLIENTE =
            "SELECT COUNT(*) FROM ISCRIZIONE_CORSO I " +
            "JOIN LEZIONE_CORSO L ON I.ID_LEZIONE = L.ID_LEZIONE " +
            "WHERE I.ID_CLIENTE = ? AND L.DATA_LEZIONE >= ?";

    // ===================== TEMPLATE PER LA CONNECTION =====================

    @FunctionalInterface
    private interface SqlAction<T> {
        T execute(Connection conn) throws Exception;
    }

    private static <T> T withConnection(SqlAction<T> action) throws Exception {
        try (Connection conn = GestioneDB.getConnection()) {
            return action.execute(conn);
        }
    }

    // ===================== UTILITY PER DESCRIZIONE PROGRAMMAZIONE =====================

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
        Map<LocalTime, List<String>> byOra = caricaMappaProgrammazione(idCorso);
        return formattaProgrammazione(byOra);
    }

    private static Map<LocalTime, List<String>> caricaMappaProgrammazione(int idCorso) throws Exception {
        Map<LocalTime, List<String>> byOra = new LinkedHashMap<>();

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_PROGRAMMAZIONE_CORSO)) {

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

    // ===================== CORSI E LEZIONI =====================

    public static List<CorsoInfo> getTuttiICorsi() throws Exception {
        return withConnection(CorsoDAO::getTuttiICorsi);
    }

    private static List<CorsoInfo> getTuttiICorsi(Connection conn) throws SQLException {
        List<CorsoInfo> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_TUTTI_I_CORSI);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID_CORSO");
                String nome = rs.getString("NOME");
                String descr = rs.getString("DESCRIZIONE");
                int durata = rs.getInt("DURATA_MINUTI");

                result.add(new CorsoInfo(id, nome, descr, durata));
            }
        }

        return result;
    }

    public static List<LezioneInfo> getLezioniPerCorso(int idCorso) throws Exception {
        LocalDate oggi = LocalDate.now();
        return withConnection(conn -> getLezioniPerCorso(conn, idCorso, oggi));
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
        withConnection(conn -> {
            aggiornaDateLezioniAllaSettimanaCorrente(conn, oggi);
            return null; // importante per far inferire T = Void
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

    // ===================== CONFLITTI E ISCRIZIONI =====================

    public static boolean esisteConflittoPerCliente(int idCliente,
                                                    LocalDate dataNuova,
                                                    LocalTime oraNuova,
                                                    int durataNuovaMin) throws Exception {

        LocalDateTime inizioNuovo = LocalDateTime.of(dataNuova, oraNuova);
        LocalDateTime fineNuovo   = inizioNuovo.plusMinutes(durataNuovaMin);

        return withConnection(conn ->
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

    // --------------------- posti disponibili ---------------------

    public static boolean haPostiDisponibili(int idLezione) throws Exception {
        return withConnection(conn -> haPostiDisponibili(conn, idLezione));
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

    // --------------------- iscrizione ---------------------

    public static void iscriviClienteALezione(int idCliente, int idLezione) throws Exception {
        withConnection(conn -> {
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

    // ===================== DISISCRIZIONE CORSI =====================

    public static List<IscrizioneInfo> getIscrizioniFuturePerCliente(int idCliente) throws Exception {
        return withConnection(conn -> getIscrizioniFuturePerCliente(conn, idCliente));
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

    public static void disiscriviClienteDaLezione(int idCliente, int idLezione) throws Exception {
        withConnection(conn -> {
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

    // ===================== DETTAGLIO CORSI PRENOTATI =====================

    public static String buildDettaglioIscrizioniPerCliente(int idCliente) throws Exception {
        return withConnection(conn -> buildDettaglioIscrizioniPerCliente(conn, idCliente));
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

    public static boolean esistonoCorsi() throws Exception {
        return withConnection(CorsoDAO::esistonoCorsi);
    }

    private static boolean esistonoCorsi(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_CORSI);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                return false;
            }
            return rs.getInt(1) > 0;
        }
    }

    public static boolean esistonoIscrizioniFuturePerCliente(int idCliente) throws Exception {
        return withConnection(conn -> esistonoIscrizioniFuturePerCliente(conn, idCliente));
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
