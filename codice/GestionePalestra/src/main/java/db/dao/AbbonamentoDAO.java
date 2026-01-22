package db.dao;

import db.GestioneDB;
import model.Abbonamento;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Date;

public class AbbonamentoDAO {

    private static final Logger logger =
            LogManager.getLogger(AbbonamentoDAO.class);

    // ===================== SQL =====================

    private static final String SQL_SELECT_ABBONAMENTO_BY_CLIENTE =
            "SELECT ID_ABBONAMENTO, TIPO, SCADENZA, " +
            "       FASCIA_ORARIA_CONSENTITA, PREZZO " +
            "FROM ABBONAMENTO " +
            "WHERE ID_CLIENTE = ?";

    private static final String SQL_INSERT_ABBONAMENTO =
            "INSERT INTO ABBONAMENTO " +
            "(TIPO, SCADENZA, ID_SPOGLIATOIO, ID_CLIENTE, " +
            " FASCIA_ORARIA_CONSENTITA, PREZZO) " +
            "VALUES (?, ?, NULL, ?, ?, ?)";

    private static final String SQL_DELETE_ISCRIZIONI_CORSO =
            "DELETE FROM ISCRIZIONE_CORSO WHERE ID_CLIENTE = ?";

    private static final String SQL_DELETE_CONSULENZE =
            "DELETE FROM CONSULENZA WHERE ID_CLIENTE = ?";

    private static final String SQL_DELETE_PAGAMENTI =
            "DELETE FROM PAGAMENTO " +
            "WHERE ID_CLIENTE = ? " +
            "AND ID_ABBONAMENTO IN (" +
            "    SELECT ID_ABBONAMENTO FROM ABBONAMENTO WHERE ID_CLIENTE = ?" +
            ")";

    private static final String SQL_DELETE_ABBONAMENTI =
            "DELETE FROM ABBONAMENTO WHERE ID_CLIENTE = ?";

    private static final String SQL_SELECT_IDCLIENTE_BY_USERNAME =
            "SELECT ID_CLIENTE FROM CLIENTE WHERE USERNAME = ?";

    // ==========================================================
    //                   LETTURA ABBONAMENTO
    // ==========================================================

    /**
     * Restituisce l'abbonamento associato a un cliente (se esiste),
     * altrimenti null.
     */
    public static Abbonamento getAbbonamentoByClienteId(int idCliente) {
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ABBONAMENTO_BY_CLIENTE)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return creaAbbonamentoDaResultSet(rs, idCliente);
            }

        } catch (SQLException e) {
            logger.error("Errore nel recupero abbonamento per cliente {}", idCliente, e);
            return null;
        }
    }

    private static Abbonamento creaAbbonamentoDaResultSet(ResultSet rs,
                                                          int idCliente) throws SQLException {
        int idAbbonamentoInt = rs.getInt("ID_ABBONAMENTO");
        String tipo          = rs.getString("TIPO");
        Date scadenza        = leggiScadenza(rs);
        String fascia        = rs.getString("FASCIA_ORARIA_CONSENTITA");
        int prezzo           = rs.getInt("PREZZO");

        Abbonamento abbonamento = Abbonamento.creaDaTipo(tipo, idCliente);
        if (abbonamento == null) {
            logger.warn("Tipo abbonamento sconosciuto '{}' per cliente {}: ritorno null",
                    tipo, idCliente);
            return null;
        }

        abbonamento.setIdAbbonamento(String.valueOf(idAbbonamentoInt));
        abbonamento.setPrezzo(prezzo);
        abbonamento.setFasciaOrariaConsentita(fascia);
        abbonamento.setScadenza(scadenza);

        return abbonamento;
    }

    private static Date leggiScadenza(ResultSet rs) throws SQLException {
        java.sql.Date sqlDate = rs.getDate("SCADENZA");
        if (sqlDate == null) {
            return null;
        }
        return new Date(sqlDate.getTime());
    }

    // ==========================================================
    //                   SALVATAGGIO ABBONAMENTO
    // ==========================================================

    /**
     * Salva su DB un nuovo abbonamento associato a un cliente.
     * Usa ID_ABBONAMENTO INT AUTO_INCREMENT generato dal database.
     */
    public static void salvaAbbonamento(Abbonamento abbonamento, int idCliente) {
        if (!isAbbonamentoValido(abbonamento, idCliente)) {
            return;
        }

        String tipo = abbonamento.getTipo();

        try (Connection conn = GestioneDB.getConnection()) {
            inserisciAbbonamento(conn, abbonamento, idCliente, tipo);
        } catch (SQLException e) {
            logger.error("Errore nel salvataggio abbonamento per cliente {}", idCliente, e);
        }
    }

    private static boolean isAbbonamentoValido(Abbonamento abbonamento, int idCliente) {
        if (abbonamento == null) {
            logger.warn("Tentativo di salvare un abbonamento null per cliente {}", idCliente);
            return false;
        }
        String tipo = abbonamento.getTipo();
        if (tipo == null || tipo.isEmpty()) {
            logger.warn("Tentativo di salvare un abbonamento senza tipo per cliente {}", idCliente);
            return false;
        }
        return true;
    }

    private static void inserisciAbbonamento(Connection conn,
                                             Abbonamento abbonamento,
                                             int idCliente,
                                             String tipo) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                SQL_INSERT_ABBONAMENTO, Statement.RETURN_GENERATED_KEYS)) {

            java.sql.Date dataSql = buildSqlDate(abbonamento.getScadenza());

            ps.setString(1, tipo);
            ps.setDate(2, dataSql);
            ps.setInt(3, idCliente);
            ps.setString(4, abbonamento.getFasciaOrariaConsentita());
            ps.setInt(5, abbonamento.getPrezzo());

            int rows = ps.executeUpdate();
            gestisciRisultatoInsert(ps, abbonamento, idCliente, tipo, rows);
        }
    }

    private static java.sql.Date buildSqlDate(Date data) {
        if (data == null) {
            return null;
        }
        return new java.sql.Date(data.getTime());
    }

    private static void gestisciRisultatoInsert(PreparedStatement ps,
                                                Abbonamento abbonamento,
                                                int idCliente,
                                                String tipo,
                                                int rows) throws SQLException {
        if (rows <= 0) {
            logger.warn("Nessun abbonamento salvato per cliente {}", idCliente);
            return;
        }

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (!rs.next()) {
                logger.warn("Abbonamento salvato per cliente {}, ma nessun ID generato trovato",
                        idCliente);
                return;
            }

            int idGen = rs.getInt(1);
            abbonamento.setIdAbbonamento(String.valueOf(idGen));

            logger.info("Abbonamento {} (tipo {}) salvato per cliente {}",
                    idGen, tipo, idCliente);
        }
    }

    // ==========================================================
    //                   DISDETTA ABBONAMENTO
    // ==========================================================

    /**
     * Disdice l'abbonamento di un cliente (ID_CLIENTE).
     * Elimina anche pagamenti, iscrizioni ai corsi e consulenze collegate.
     */
    public static void disdiciAbbonamentoPerCliente(int idCliente) {

        try (Connection conn = GestioneDB.getConnection()) {

            try {
                conn.setAutoCommit(false);

                eseguiDisdettaAbbonamento(conn, idCliente);

                conn.commit();
                logger.info("Abbonamento, pagamenti, corsi e consulenze disdetti per cliente {}",
                        idCliente);

            } catch (SQLException e) {
                conn.rollback();
                logger.error("Errore durante la disdetta abbonamento per cliente {}", idCliente, e);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logger.error("Errore di connessione in disdiciAbbonamentoPerCliente", e);
        }
    }

    /**
     * Disdice l'abbonamento partendo dall'USERNAME del cliente.
     * Elimina anche pagamenti, iscrizioni ai corsi e consulenze collegate.
     */
    public static void disdiciAbbonamentoPerUsername(String username) {

        Integer idCliente = getIdClienteByUsername(username);
        if (idCliente == null) {
            logger.warn("Nessun cliente trovato per username {} in disdiciAbbonamentoPerUsername",
                    username);
            return;
        }

        disdiciAbbonamentoPerCliente(idCliente);
    }

    private static Integer getIdClienteByUsername(String username) {
        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_IDCLIENTE_BY_USERNAME)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getInt("ID_CLIENTE");
            }

        } catch (SQLException e) {
            logger.error("Errore nel recuperare ID_CLIENTE per username {}", username, e);
            return null;
        }
    }

    /**
     * Esegue fisicamente le DELETE su tutte le tabelle collegate all'abbonamento.
     * (iscrizioni corsi, consulenze, pagamenti, abbonamenti).
     */
    private static void eseguiDisdettaAbbonamento(Connection conn,
                                                  int idCliente) throws SQLException {
        deleteIscrizioniCorso(conn, idCliente);
        deleteConsulenze(conn, idCliente);
        deletePagamenti(conn, idCliente);
        deleteAbbonamenti(conn, idCliente);
    }

    private static void deleteIscrizioniCorso(Connection conn,
                                              int idCliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ISCRIZIONI_CORSO)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    private static void deleteConsulenze(Connection conn,
                                         int idCliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_CONSULENZE)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    private static void deletePagamenti(Connection conn,
                                        int idCliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_PAGAMENTI)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idCliente);
            ps.executeUpdate();
        }
    }

    private static void deleteAbbonamenti(Connection conn,
                                          int idCliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ABBONAMENTI)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }
}
