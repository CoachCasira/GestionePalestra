package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Classe di supporto che si occupa solo di inizializzare
 * corsi e lezioni, per tenere snella InizializzazioneDB.
 */
final class InizializzazioneCorsiDB {

    private InizializzazioneCorsiDB() {
        // utility class
    }

    static void popolaCorsiELezioni(Connection conn) throws SQLException {
        // Se ci sono già corsi, non facciamo nulla
        if (!InizializzazioneDB.isTableEmpty(conn, "CORSO")) {
            return;
        }

        // 1) inserimento corsi base
        int[] idsCorsi = inserisciCorsiBase(conn);
        int idSpinning = idsCorsi[0];
        int idPilates  = idsCorsi[1];
        int idAcquaGym = idsCorsi[2];

        // 2) inserimento lezioni solo se la tabella è vuota
        if (!InizializzazioneDB.isTableEmpty(conn, "LEZIONE_CORSO")) {
            return;
        }

        inserisciLezioniBase(conn, idSpinning, idPilates, idAcquaGym);
    }

    // =========================================================
    //  CORSI
    // =========================================================

    private static int[] inserisciCorsiBase(Connection conn) throws SQLException {
        int idSpinning = inserisciSingoloCorso(
                conn,
                "Spinning",
                "Allenamento cardiovascolare su bike indoor ad alta intensità.",
                60
        );

        int idPilates = inserisciSingoloCorso(
                conn,
                "Pilates",
                "Corso di tonificazione e postura basato su esercizi a corpo libero.",
                60
        );

        int idAcquaGym = inserisciSingoloCorso(
                conn,
                "AcquaGym",
                "Allenamento aerobico in acqua a basso impatto articolare.",
                60
        );

        return new int[]{idSpinning, idPilates, idAcquaGym};
    }

    private static int inserisciSingoloCorso(Connection conn,
                                             String nome,
                                             String descrizione,
                                             int durataMinuti) throws SQLException {
        String sql =
                "INSERT INTO CORSO (NOME, DESCRIZIONE, DURATA_MINUTI) " +
                        "VALUES (?, ?, ?)";

        try (PreparedStatement ps =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nome);
            ps.setString(2, descrizione);
            ps.setInt(3, durataMinuti);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    // =========================================================
    //  LEZIONI
    // =========================================================

    private static void inserisciLezioniBase(Connection conn,
                                             int idSpinning,
                                             int idPilates,
                                             int idAcquaGym) throws SQLException {

        int[] istruttori = caricaIstruttoriCorso(conn);
        int istr1 = istruttori[0];
        int istr2 = istruttori[1];
        int istr3 = istruttori[2];

        int fallback = istr1 != -1 ? istr1 : (istr2 != -1 ? istr2 : istr3);

        inserisciLezioniSpinning(conn, idSpinning, istr1, fallback);
        inserisciLezioniPilates(conn, idPilates, istr2, fallback);
        inserisciLezioniAcquaGym(conn, idAcquaGym, istr3, fallback);
    }

    private static void inserisciLezioniSpinning(Connection conn,
                                                 int idSpinning,
                                                 int idIstruttore,
                                                 int fallback) throws SQLException {
        if (idSpinning <= 0) {
            return;
        }

        LocalDate oggi = LocalDate.now();
        LocalDate lun  = next(oggi, DayOfWeek.MONDAY);
        LocalDate mer  = next(oggi, DayOfWeek.WEDNESDAY);
        LocalDate ven  = next(oggi, DayOfWeek.FRIDAY);

        int istr = idIstruttore != -1 ? idIstruttore : fallback;

        try (PreparedStatement psL = preparaStatementLezione(conn)) {
            inserisciSingolaLezione(psL, idSpinning, lun,  "18:00:00", 25, istr);
            inserisciSingolaLezione(psL, idSpinning, mer,  "18:00:00", 25, istr);
            inserisciSingolaLezione(psL, idSpinning, ven,  "18:00:00", 25, istr);
        }
    }

    private static void inserisciLezioniPilates(Connection conn,
                                                int idPilates,
                                                int idIstruttore,
                                                int fallback) throws SQLException {
        if (idPilates <= 0) {
            return;
        }

        LocalDate oggi = LocalDate.now();
        LocalDate mar  = next(oggi, DayOfWeek.TUESDAY);
        LocalDate gio  = next(oggi, DayOfWeek.THURSDAY);

        int istr = idIstruttore != -1 ? idIstruttore : fallback;

        try (PreparedStatement psL = preparaStatementLezione(conn)) {
            inserisciSingolaLezione(psL, idPilates, mar, "19:00:00", 20, istr);
            inserisciSingolaLezione(psL, idPilates, gio, "19:00:00", 20, istr);
        }
    }

    private static void inserisciLezioniAcquaGym(Connection conn,
                                                 int idAcquaGym,
                                                 int idIstruttore,
                                                 int fallback) throws SQLException {
        if (idAcquaGym <= 0) {
            return;
        }

        LocalDate oggi = LocalDate.now();
        LocalDate sab  = next(oggi, DayOfWeek.SATURDAY);

        int istr = idIstruttore != -1 ? idIstruttore : fallback;

        try (PreparedStatement psL = preparaStatementLezione(conn)) {
            inserisciSingolaLezione(psL, idAcquaGym, sab, "10:00:00", 15, istr);
        }
    }

    private static PreparedStatement preparaStatementLezione(Connection conn) throws SQLException {
        String sqlInsLez =
                "INSERT INTO LEZIONE_CORSO " +
                        "(ID_CORSO, DATA_LEZIONE, ORA_LEZIONE, POSTI_TOTALI, POSTI_PRENOTATI, ID_ISTRUTTORE) " +
                        "VALUES (?, ?, ?, ?, 0, ?)";
        return conn.prepareStatement(sqlInsLez);
    }

    // =========================================================
    //  UTILITY INTERNE
    // =========================================================

    private static int[] caricaIstruttoriCorso(Connection conn) throws SQLException {
        int istr1 = -1;
        int istr2 = -1;
        int istr3 = -1;

        String sqlIstr =
                "SELECT ID_DIPENDENTE FROM DIPENDENTE " +
                        "WHERE RUOLO = 'ISTRUTTORE_CORSO' ORDER BY ID_DIPENDENTE";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sqlIstr)) {

            if (rs.next()) istr1 = rs.getInt(1);
            if (rs.next()) istr2 = rs.getInt(1);
            if (rs.next()) istr3 = rs.getInt(1);
        }

        return new int[]{istr1, istr2, istr3};
    }

    private static void inserisciSingolaLezione(PreparedStatement psL,
                                                int idCorso,
                                                LocalDate data,
                                                String ora,
                                                int postiTotali,
                                                int idIstruttore) throws SQLException {
        psL.setInt(1, idCorso);
        psL.setDate(2, Date.valueOf(data));
        psL.setTime(3, Time.valueOf(ora));
        psL.setInt(4, postiTotali);
        psL.setInt(5, idIstruttore);
        psL.executeUpdate();
    }

    private static LocalDate next(LocalDate from, DayOfWeek target) {
        int diff = target.getValue() - from.getDayOfWeek().getValue();
        if (diff <= 0) {
            diff += 7;
        }
        return from.plusDays(diff);
    }
}
