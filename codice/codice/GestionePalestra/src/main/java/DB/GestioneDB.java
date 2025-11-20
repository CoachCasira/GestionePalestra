package DB;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestioneDB {
	
	 // Il file del DB verrà creato nella cartella "data" del progetto
    private static final String JDBC_URL = "jdbc:h2:./data/palestra;AUTO_SERVER=TRUE";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    static {
        try {
            // Carica il driver H2
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Impossibile caricare il driver H2", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    public static void stampaClienti() {
        String sql = "SELECT ID_CLIENTE, USERNAME, NOME, COGNOME FROM CLIENTE";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    "ID_CLIENTE: "+rs.getInt("ID_CLIENTE") + " - " +
                    "USERNAME: "+rs.getString("USERNAME") + " - " +
                    "NOME: "+rs.getString("NOME") + " - " +
                    "COGNOME: "+rs.getString("COGNOME")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
