package db.dao;

import db.GestioneDB;
import model.Consulenza;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class ConsulenzaDAO {

    // ==========================================================
    //  DTO interno comodo per la disdetta
    // ==========================================================
    public static class ConsulenzaInfo {
        public final int id;
        public final LocalDate data;
        public final LocalTime ora;
        public final String tipo;
        public final String nomeDip;
        public final String ruoloDip;
        public final String note;
        public final int durataMinuti;

        public ConsulenzaInfo(int id,
                              LocalDate data,
                              LocalTime ora,
                              String tipo,
                              String nomeDip,
                              String ruoloDip,
                              String note,
                              int durataMinuti) {
            this.id = id;
            this.data = data;
            this.ora = ora;
            this.tipo = tipo;
            this.nomeDip = nomeDip;
            this.ruoloDip = ruoloDip;
            this.note = note;
            this.durataMinuti = durataMinuti;
        }

        public LocalDateTime getDataOra() {
            return LocalDateTime.of(data, ora);
        }
    }

    // ==========================================================
    //  COSTANTI SQL CONDIVISE
    // ==========================================================
    private static final String SQL_SELECT_CONSULENZE_BASE =
            "SELECT C.ID_CONSULENZA, C.TIPO, C.DATA_CONSULENZA, C.ORA_CONSULENZA, C.NOTE, " +
            "       D.NOME, D.COGNOME, D.RUOLO " +
            "FROM CONSULENZA C " +
            "JOIN DIPENDENTE D ON C.ID_DIPENDENTE = D.ID_DIPENDENTE " +
            "WHERE C.ID_CLIENTE = ? AND C.DATA_CONSULENZA ";

    private static final String SQL_ORDER_FUTURE =
            "ORDER BY C.DATA_CONSULENZA, C.ORA_CONSULENZA";

    private static final String SQL_ORDER_PAST =
            "ORDER BY C.DATA_CONSULENZA DESC, C.ORA_CONSULENZA DESC";

    private static final String SQL_CONFLITTO =
            "SELECT ID_CLIENTE, ID_DIPENDENTE, TIPO, DATA_CONSULENZA, ORA_CONSULENZA " +
            "FROM CONSULENZA " +
            "WHERE DATA_CONSULENZA = ? " +
            "  AND (ID_CLIENTE = ? OR ID_DIPENDENTE = ?)";

    private static final String SQL_INSERISCI =
            "INSERT INTO CONSULENZA " +
            "(ID_CLIENTE, ID_DIPENDENTE, TIPO, DATA_CONSULENZA, ORA_CONSULENZA, NOTE) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_CONSULENZE_FUTURE_COUNT =
            "SELECT COUNT(*) FROM CONSULENZA " +
            "WHERE ID_CLIENTE = ? AND DATA_CONSULENZA >= ?";

    private static final String SQL_DELETE_CONSULENZA =
            "DELETE FROM CONSULENZA WHERE ID_CONSULENZA = ?";

    // ==========================================================
    //  CONTROLLO CONFLITTO PER INSERIMENTO NUOVA CONSULENZA
    // ==========================================================
    /**
     * Ritorna true se esiste un conflitto di orario:
     * - stesso CLIENTE con un’altra consulenza sovrapposta
     *   (anche con dipendente diverso)
     * - stesso DIPENDENTE con un’altra consulenza sovrapposta
     *   (anche con cliente diverso)
     *
     * Il controllo è fatto considerando la durata stimata del tipo
     * di consulenza.
     */
    public static boolean esisteConflitto(int idCliente,
                                          int idDipendente,
                                          String tipoNuovo,
                                          LocalDate data,
                                          LocalTime oraInizioNuova) throws Exception {

        int durataNuova = durataStimataMinuti(tipoNuovo);
        LocalDateTime startNuova = LocalDateTime.of(data, oraInizioNuova);
        LocalDateTime endNuova   = startNuova.plusMinutes(durataNuova);

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CONFLITTO)) {

            ps.setDate(1, Date.valueOf(data));
            ps.setInt(2, idCliente);
            ps.setInt(3, idDipendente);

            try (ResultSet rs = ps.executeQuery()) {
                return hasConflictWithExisting(startNuova, endNuova, rs);
            }
        }
    }

    // Verifica sovrapposizioni con le consulenze già presenti a DB
    private static boolean hasConflictWithExisting(LocalDateTime startNuova,
                                                   LocalDateTime endNuova,
                                                   ResultSet rs) throws SQLException {
        while (rs.next()) {
            String tipoEsistente = rs.getString("TIPO");
            LocalDate dataEsist  = rs.getDate("DATA_CONSULENZA").toLocalDate();
            LocalTime oraEsist   = rs.getTime("ORA_CONSULENZA").toLocalTime();

            int durataEsistente = durataStimataMinuti(tipoEsistente);

            LocalDateTime startEsistente = LocalDateTime.of(dataEsist, oraEsist);
            LocalDateTime endEsistente   = startEsistente.plusMinutes(durataEsistente);

            if (overlap(startNuova, endNuova, startEsistente, endEsistente)) {
                return true;
            }
        }
        return false;
    }

    // Overlap tra due intervalli [s1,e1) e [s2,e2)
    private static boolean overlap(LocalDateTime s1, LocalDateTime e1,
                                   LocalDateTime s2, LocalDateTime e2) {
        return !(e1.compareTo(s2) <= 0 || s1.compareTo(e2) >= 0);
    }

    // ==========================================================
    //  INSERIMENTO NUOVA CONSULENZA
    // ==========================================================
    public static void inserisci(Consulenza c) throws Exception {
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERISCI)) {

            ps.setInt(1, c.getIdCliente());
            ps.setInt(2, c.getIdDipendente());
            ps.setString(3, c.getTipo());
            ps.setDate(4, Date.valueOf(c.getData()));
            ps.setTime(5, Time.valueOf(c.getOra()));
            ps.setString(6, c.getNote());

            ps.executeUpdate();
        }
    }

    // ==========================================================
    //  TESTO DETTAGLI CONSULENZE (HOME)
    // ==========================================================
    public static String buildDettaglioConsulenzePerCliente(int idCliente) throws Exception {
        LocalDate today = LocalDate.now();
        StringBuilder sb = new StringBuilder();

        // Future / programmate
        List<ConsulenzaInfo> future =
                caricaConsulenzePerCliente(idCliente, today, true);

        sb.append("CONSULENZE FUTURE / PROGRAMMATE\n\n");
        appendConsulenzeDettaglio(sb, future, "Nessuna consulenza futura programmata.");

        // Passate
        List<ConsulenzaInfo> past =
                caricaConsulenzePerCliente(idCliente, today, false);

        sb.append("-------------------------------------\n");
        sb.append("CONSULENZE PASSATE\n\n");
        appendConsulenzeDettaglio(sb, past, "Nessuna consulenza passata registrata.\n");

        return sb.toString();
    }

    // Carica consulenze future o passate per un cliente
    private static List<ConsulenzaInfo> caricaConsulenzePerCliente(int idCliente,
                                                                   LocalDate dataRiferimento,
                                                                   boolean future) throws Exception {
        String operatore = future ? ">= ?" : "< ?";
        String orderBy   = future ? SQL_ORDER_FUTURE : SQL_ORDER_PAST;

        String sql = SQL_SELECT_CONSULENZE_BASE + operatore + " " + orderBy;

        List<ConsulenzaInfo> list = new ArrayList<>();

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(dataRiferimento));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToConsulenzaInfo(rs));
                }
            }
        }
        return list;
    }

    // Mappa una riga di ResultSet in ConsulenzaInfo
    private static ConsulenzaInfo mapRowToConsulenzaInfo(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_CONSULENZA");
        LocalDate data = rs.getDate("DATA_CONSULENZA").toLocalDate();
        LocalTime ora = rs.getTime("ORA_CONSULENZA").toLocalTime();
        String tipo = rs.getString("TIPO");
        String note = rs.getString("NOTE");
        String nome = rs.getString("NOME");
        String cognome = rs.getString("COGNOME");
        String ruolo = rs.getString("RUOLO");

        int durata = durataStimataMinuti(tipo);

        return new ConsulenzaInfo(
                id,
                data,
                ora,
                tipo,
                nome + " " + cognome,
                ruolo,
                note,
                durata
        );
    }

    // Scrive nel buffer il dettaglio testuale di una lista di consulenze
    private static void appendConsulenzeDettaglio(StringBuilder sb,
                                                  List<ConsulenzaInfo> consulenze,
                                                  String messaggioVuoto) {
        if (consulenze.isEmpty()) {
            sb.append(messaggioVuoto).append("\n");
            return;
        }

        for (ConsulenzaInfo c : consulenze) {
            sb.append("- ")
              .append(c.data).append(" ore ").append(c.ora)
              .append("\nTipo: ").append(c.tipo)
              .append("\nProfessionista: ").append(c.nomeDip)
              .append(" (").append(c.ruoloDip).append(")")
              .append("\nDurata stimata: ")
              .append(c.durataMinuti).append(" minuti")
              .append("\nNote: ")
              .append(c.note == null || c.note.isEmpty() ? "Nessuna nota." : c.note)
              .append("\n\n");
        }
    }

    // ==========================================================
    //  DURATA STIMATA PER TIPO DI CONSULENZA
    // ==========================================================
    public static int durataStimataMinuti(String tipo) {
        if (tipo == null) {
            return 30;
        }
        String t = tipo.toUpperCase();
        if (t.contains("NUTR")) {
            return 60;          // nutrizionista
        }
        if (t.contains("ISTRUTTORE")) {
            return 45;          // istruttore corso
        }
        return 30;              // personal trainer di default
    }

    // ==========================================================
    //  ESISTONO CONSULENZE FUTURE PER IL CLIENTE?
    // ==========================================================
    public static boolean esistonoConsulenzeFuturePerCliente(int idCliente) throws Exception {
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CONSULENZE_FUTURE_COUNT)) {

            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // ==========================================================
    //  ELENCO CONSULENZE FUTURE (PER LA DISDETTA)
    // ==========================================================
    public static List<ConsulenzaInfo> getConsulenzeFuturePerCliente(int idCliente) throws Exception {
        return caricaConsulenzePerCliente(idCliente, LocalDate.now(), true);
    }

    // ==========================================================
    //  CANCELLAZIONE DI UNA SINGOLA CONSULENZA
    // ==========================================================
    public static void disdiciConsulenza(int idConsulenza) throws Exception {
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_CONSULENZA)) {

            ps.setInt(1, idConsulenza);
            ps.executeUpdate();
        }
    }
}
