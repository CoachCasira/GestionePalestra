package db.dao.corso;

import db.dao.UtilsDAO;
import model.corsi.CorsoInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CorsoDAO {

    private static final String SQL_TUTTI_I_CORSI =
            "SELECT ID_CORSO, NOME, DESCRIZIONE, DURATA_MINUTI " +
            "FROM CORSO ORDER BY NOME";

    private static final String SQL_COUNT_CORSI =
            "SELECT COUNT(*) FROM CORSO";

    // ===================== CORSI =====================

    public static List<CorsoInfo> getTuttiICorsi() throws Exception {
        return UtilsDAO.withConnection(CorsoDAO::getTuttiICorsi);
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

    // ===================== CHECK DI ESISTENZA =====================

    public static boolean esistonoCorsi() throws Exception {
        return UtilsDAO.withConnection(CorsoDAO::esistonoCorsi);
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
}
