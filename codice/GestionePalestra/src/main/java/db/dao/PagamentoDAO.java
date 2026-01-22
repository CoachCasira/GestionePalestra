package db.dao;

import db.GestioneDB;
import model.Pagamento;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Date;

public class PagamentoDAO {

    private static final Logger logger =
            LogManager.getLogger(PagamentoDAO.class);

    /**
     * Salva un pagamento associandolo a cliente e abbonamento.
     */
    public static void salvaPagamento(Pagamento pagamento,
                                      int idCliente,
                                      String idAbbonamento) {

        if (pagamento == null || idAbbonamento == null) {
            return;
        }

        String sql = "INSERT INTO PAGAMENTO " +
                     "(METODO, IMPORTO, DATA_PAGAMENTO, ID_CLIENTE, ID_ABBONAMENTO) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pagamento.getMetodo());
            ps.setInt(2, (int) pagamento.getImporto());

            Date data = pagamento.getDataPagamento();
            java.sql.Date dataSql = new java.sql.Date(data.getTime());
            ps.setDate(3, dataSql);

            ps.setInt(4, idCliente);
            ps.setString(5, idAbbonamento);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                logger.info("Pagamento salvato per cliente {} e abbonamento {}",
                        idCliente, idAbbonamento);
            } else {
                logger.warn("Nessun pagamento salvato per cliente {}", idCliente);
            }

        } catch (SQLException e) {
            logger.error("Errore nel salvataggio pagamento per cliente {}", idCliente, e);
        }
    }
}
