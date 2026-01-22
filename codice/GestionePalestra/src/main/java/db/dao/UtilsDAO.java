package db.dao;

import db.GestioneDB;

import java.sql.Connection;

/**
 * Utility condivisa per gestire l'apertura/chiusura della Connection.
 */
public final class UtilsDAO {

    private UtilsDAO() {
        // utility class
    }

    @FunctionalInterface
    public interface SqlAction<T> {
        T execute(Connection conn) throws Exception;
    }

    public static <T> T withConnection(SqlAction<T> action) throws Exception {
        try (Connection conn = GestioneDB.getConnection()) {
            return action.execute(conn);
        }
    }
}
