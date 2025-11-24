package db.dao;

import db.GestioneDB;
import model.Consulenza;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ConsulenzaDAO {

    // DTO interno comodo per la disdetta
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

    // -----------------------------------------------------------
    //  CONTROLLO CONFLITTO PER INSERIMENTO NUOVA CONSULENZA
    // -----------------------------------------------------------
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

        // intervallo della nuova consulenza
        int durataNuova = durataStimataMinuti(tipoNuovo);
        LocalDateTime startNuova = LocalDateTime.of(data, oraInizioNuova);
        LocalDateTime endNuova   = startNuova.plusMinutes(durataNuova);

        String sql =
                "SELECT ID_CLIENTE, ID_DIPENDENTE, TIPO, DATA_CONSULENZA, ORA_CONSULENZA " +
                "FROM CONSULENZA " +
                "WHERE DATA_CONSULENZA = ? " +
                "  AND (ID_CLIENTE = ? OR ID_DIPENDENTE = ?)";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(data));
            ps.setInt(2, idCliente);
            ps.setInt(3, idDipendente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tipoEsistente = rs.getString("TIPO");
                    LocalDate dataEsist  = rs.getDate("DATA_CONSULENZA").toLocalDate();
                    LocalTime oraEsist   = rs.getTime("ORA_CONSULENZA").toLocalTime();

                    int durataEsistente = durataStimataMinuti(tipoEsistente);

                    LocalDateTime startEsistente = LocalDateTime.of(dataEsist, oraEsist);
                    LocalDateTime endEsistente   = startEsistente.plusMinutes(durataEsistente);

                    // overlap se gli intervalli NON sono disgiunti
                    boolean nonOverlap =
                            endNuova.compareTo(startEsistente) <= 0 ||
                            startNuova.compareTo(endEsistente) >= 0;

                    if (!nonOverlap) {
                        // c'è sovrapposizione di orario, quindi conflitto
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // -----------------------------------------------------------
    //  INSERIMENTO NUOVA CONSULENZA
    // -----------------------------------------------------------
    public static void inserisci(Consulenza c) throws Exception {
        String sql = "INSERT INTO CONSULENZA " +
                     "(ID_CLIENTE, ID_DIPENDENTE, TIPO, DATA_CONSULENZA, ORA_CONSULENZA, NOTE) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            ps.setInt(2, c.getIdDipendente());
            ps.setString(3, c.getTipo());
            ps.setDate(4, Date.valueOf(c.getData()));
            ps.setTime(5, Time.valueOf(c.getOra()));
            ps.setString(6, c.getNote());

            ps.executeUpdate();
        }
    }

    // -----------------------------------------------------------
    //  TESTO DETTAGLI CONSULENZE (HOME)
    // -----------------------------------------------------------
    public static String buildDettaglioConsulenzePerCliente(int idCliente) throws Exception {
        StringBuilder sb = new StringBuilder();

        LocalDate today = LocalDate.now();

        // future / programmate
        String sqlFuture =
                "SELECT C.ID_CONSULENZA, C.TIPO, C.DATA_CONSULENZA, C.ORA_CONSULENZA, C.NOTE, " +
                "       D.NOME, D.COGNOME, D.RUOLO " +
                "FROM CONSULENZA C " +
                "JOIN DIPENDENTE D ON C.ID_DIPENDENTE = D.ID_DIPENDENTE " +
                "WHERE C.ID_CLIENTE = ? AND C.DATA_CONSULENZA >= ? " +
                "ORDER BY C.DATA_CONSULENZA, C.ORA_CONSULENZA";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFuture)) {

            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(today));

            try (ResultSet rs = ps.executeQuery()) {
                sb.append("CONSULENZE FUTURE / PROGRAMMATE\n\n");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    LocalDate data = rs.getDate("DATA_CONSULENZA").toLocalDate();
                    LocalTime ora = rs.getTime("ORA_CONSULENZA").toLocalTime();
                    String tipo = rs.getString("TIPO");
                    String note = rs.getString("NOTE");
                    String nome = rs.getString("NOME");
                    String cognome = rs.getString("COGNOME");
                    String ruolo = rs.getString("RUOLO");

                    int durata = durataStimataMinuti(tipo);

                    sb.append("- ")
                      .append(data).append(" ore ").append(ora)
                      .append("\nTipo: ").append(tipo)
                      .append("\nProfessionista: ")
                      .append(nome).append(" ").append(cognome)
                      .append(" (").append(ruolo).append(")")
                      .append("\nDurata stimata: ")
                      .append(durata).append(" minuti")
                      .append("\nNote: ")
                      .append(note == null || note.isEmpty() ? "Nessuna nota." : note)
                      .append("\n\n");
                }
                if (!any) {
                    sb.append("Nessuna consulenza futura programmata.\n\n");
                }
            }
        }

        // passate
        String sqlPast =
                "SELECT C.ID_CONSULENZA, C.TIPO, C.DATA_CONSULENZA, C.ORA_CONSULENZA, C.NOTE, " +
                "       D.NOME, D.COGNOME, D.RUOLO " +
                "FROM CONSULENZA C " +
                "JOIN DIPENDENTE D ON C.ID_DIPENDENTE = D.ID_DIPENDENTE " +
                "WHERE C.ID_CLIENTE = ? AND C.DATA_CONSULENZA < ? " +
                "ORDER BY C.DATA_CONSULENZA DESC, C.ORA_CONSULENZA DESC";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPast)) {

            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(today));

            sb.append("-------------------------------------\n");
            sb.append("CONSULENZE PASSATE\n\n");

            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    LocalDate data = rs.getDate("DATA_CONSULENZA").toLocalDate();
                    LocalTime ora = rs.getTime("ORA_CONSULENZA").toLocalTime();
                    String tipo = rs.getString("TIPO");
                    String note = rs.getString("NOTE");
                    String nome = rs.getString("NOME");
                    String cognome = rs.getString("COGNOME");
                    String ruolo = rs.getString("RUOLO");

                    int durata = durataStimataMinuti(tipo);

                    sb.append("- ")
                      .append(data).append(" ore ").append(ora)
                      .append("\nTipo: ").append(tipo)
                      .append("\nProfessionista: ")
                      .append(nome).append(" ").append(cognome)
                      .append(" (").append(ruolo).append(")")
                      .append("\nDurata stimata: ")
                      .append(durata).append(" minuti")
                      .append("\nNote: ")
                      .append(note == null || note.isEmpty() ? "Nessuna nota." : note)
                      .append("\n\n");
                }
                if (!any) {
                    sb.append("Nessuna consulenza passata registrata.\n");
                }
            }
        }

        return sb.toString();
    }

    // ---------- durata stimata per tipo di consulenza ----------
    public static int durataStimataMinuti(String tipo) {
        if (tipo == null) return 30;
        String t = tipo.toUpperCase();
        if (t.contains("NUTR")) return 60;          // nutrizionista
        if (t.contains("ISTRUTTORE")) return 45;    // istruttore corso
        return 30;                                  // personal trainer di default
    }

    // ---------- esistono consulenze future per il cliente? ----------
    public static boolean esistonoConsulenzeFuturePerCliente(int idCliente) throws Exception {
        String sql = "SELECT COUNT(*) FROM CONSULENZA " +
                     "WHERE ID_CLIENTE = ? AND DATA_CONSULENZA >= ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // ---------- elenco consulenze future (per la disdetta) ----------
    public static List<ConsulenzaInfo> getConsulenzeFuturePerCliente(int idCliente) throws Exception {
        String sql =
                "SELECT C.ID_CONSULENZA, C.TIPO, C.DATA_CONSULENZA, C.ORA_CONSULENZA, C.NOTE, " +
                "       D.NOME, D.COGNOME, D.RUOLO " +
                "FROM CONSULENZA C " +
                "JOIN DIPENDENTE D ON C.ID_DIPENDENTE = D.ID_DIPENDENTE " +
                "WHERE C.ID_CLIENTE = ? AND C.DATA_CONSULENZA >= ? " +
                "ORDER BY C.DATA_CONSULENZA, C.ORA_CONSULENZA";

        List<ConsulenzaInfo> list = new ArrayList<>();

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID_CONSULENZA");
                    LocalDate data = rs.getDate("DATA_CONSULENZA").toLocalDate();
                    LocalTime ora = rs.getTime("ORA_CONSULENZA").toLocalTime();
                    String tipo = rs.getString("TIPO");
                    String note = rs.getString("NOTE");
                    String nome = rs.getString("NOME");
                    String cognome = rs.getString("COGNOME");
                    String ruolo = rs.getString("RUOLO");

                    int durata = durataStimataMinuti(tipo);

                    list.add(new ConsulenzaInfo(
                            id, data, ora, tipo,
                            nome + " " + cognome,
                            ruolo,
                            note,
                            durata
                    ));
                }
            }
        }

        return list;
    }

    // ---------- cancellazione di una singola consulenza ----------
    public static void disdiciConsulenza(int idConsulenza) throws Exception {
        String sql = "DELETE FROM CONSULENZA WHERE ID_CONSULENZA = ?";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idConsulenza);
            ps.executeUpdate();
        }
    }
}
