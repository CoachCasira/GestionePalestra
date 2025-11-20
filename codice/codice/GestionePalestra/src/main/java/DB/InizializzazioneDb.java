package DB;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
public class InizializzazioneDb {

	
	  public static void init() {
	     // 1 - SPOGLIATOIO
	        String sqlSpogliatoio = "CREATE TABLE IF NOT EXISTS SPOGLIATOIO (" +
	                "ID_SPOGLIATOIO INT AUTO_INCREMENT PRIMARY KEY, " +
	                "NUM_ARMADIETTI INT NOT NULL, " +
	                "NUM_DOCCE INT NOT NULL, " +
	                "NUM_ARMADIETTI_LIBERI INT NOT NULL" +
	                ");";

	        // 2 - SALA
	        String sqlSala = "CREATE TABLE IF NOT EXISTS SALA (" +
	                "ID_SALA INT AUTO_INCREMENT PRIMARY KEY, " +
	                "ORARI_APERTURA VARCHAR(100) NOT NULL, " +
	                "CAPIENZA INT NOT NULL, " +
	                "DISPONIBILITA BOOLEAN NOT NULL" +
	                ");";

	        // 3 - SALA_PESI
	        String sqlSalaPesi = "CREATE TABLE IF NOT EXISTS SALA_PESI (" +
	                "ID_SALA INT PRIMARY KEY, " +
	                "METRATURA INT NOT NULL, " +
	                "NUM_MACCHINARI INT NOT NULL, " +
	                "NUM_PANCHE INT NOT NULL, " +
	                "NUM_PESI_LIBERI INT NOT NULL, " +
	                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
	                ");";

	        // 4 - SALA_CORSI
	        String sqlSalaCorsi = "CREATE TABLE IF NOT EXISTS SALA_CORSI (" +
	                "ID_SALA INT PRIMARY KEY, " +
	                "ORARIO_CORSO VARCHAR(100) NOT NULL, " +
	                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
	                ");";

	        // 5 - SPA
	        String sqlSpa = "CREATE TABLE IF NOT EXISTS SPA (" +
	                "ID_SALA INT PRIMARY KEY, " +
	                "NUM_SAUNE INT NOT NULL, " +
	                "NUM_PISCINE INT NOT NULL, " +
	                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
	                ");";

	        // 6 - CLIENTE (CON luogoNascita AGGIUNTO)
	        String sqlCliente = "CREATE TABLE IF NOT EXISTS CLIENTE (" +
	                "ID_CLIENTE INT AUTO_INCREMENT PRIMARY KEY, " +
	                "USERNAME VARCHAR(50) NOT NULL UNIQUE, " +     // <— NUOVO
	                "NOME VARCHAR(100) NOT NULL, " +
	                "COGNOME VARCHAR(100) NOT NULL, " +
	                "CF VARCHAR(16) NOT NULL UNIQUE, " +
	                "LUOGO_NASCITA VARCHAR(100) NOT NULL, " +
	                "DATA_NASCITA DATE NOT NULL, " +
	                "IBAN VARCHAR(34), " +
	                "PASSWORD VARCHAR(100) NOT NULL" +
	                ");";


	        // 7 - ABBONAMENTO
	        String sqlAbbonamento = "CREATE TABLE IF NOT EXISTS ABBONAMENTO (" +
	                "ID_ABBONAMENTO VARCHAR(50) PRIMARY KEY, " +
	                "SCADENZA DATE NOT NULL, " +
	                "ID_SPOGLIATOIO INT, " +
	                "ID_CLIENTE INT NOT NULL, " +
	                "FASCIA_ORARIA_CONSENTITA VARCHAR(100), " +
	                "PREZZO INT NOT NULL, " +
	                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE), " +
	                "FOREIGN KEY (ID_SPOGLIATOIO) REFERENCES SPOGLIATOIO(ID_SPOGLIATOIO)" +
	                ");";

	        // 8 - SOTTOCLASSI ABBONAMENTO
	        String sqlAbbonamentoBasico = "CREATE TABLE IF NOT EXISTS ABBONAMENTO_BASICO (" +
	                "ID_ABBONAMENTO VARCHAR(50) PRIMARY KEY, " +
	                "ID_SALA_PESI INT NOT NULL, " +
	                "LIMITE_INGRESSI_MENSILI INT NOT NULL, " +
	                "FOREIGN KEY (ID_ABBONAMENTO) REFERENCES ABBONAMENTO(ID_ABBONAMENTO), " +
	                "FOREIGN KEY (ID_SALA_PESI) REFERENCES SALA_PESI(ID_SALA)" +
	                ");";

	        String sqlAbbonamentoCompleto = "CREATE TABLE IF NOT EXISTS ABBONAMENTO_COMPLETO (" +
	                "ID_ABBONAMENTO VARCHAR(50) PRIMARY KEY, " +
	                "ID_SALA INT NOT NULL, " +
	                "SOGLIA_SCONTO INT NOT NULL, " +
	                "FOREIGN KEY (ID_ABBONAMENTO) REFERENCES ABBONAMENTO(ID_ABBONAMENTO), " +
	                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
	                ");";

	        String sqlAbbonamentoCorsi = "CREATE TABLE IF NOT EXISTS ABBONAMENTO_CORSI (" +
	                "ID_ABBONAMENTO VARCHAR(50) PRIMARY KEY, " +
	                "ID_SALA_CORSI INT NOT NULL, " +
	                "NUM_CORSI_INCLUSI INT NOT NULL, " +
	                "FOREIGN KEY (ID_ABBONAMENTO) REFERENCES ABBONAMENTO(ID_ABBONAMENTO), " +
	                "FOREIGN KEY (ID_SALA_CORSI) REFERENCES SALA_CORSI(ID_SALA)" +
	                ");";

	        // 9 - PAGAMENTO
	        String sqlPagamento = "CREATE TABLE IF NOT EXISTS PAGAMENTO (" +
	                "ID_PAGAMENTO INT AUTO_INCREMENT PRIMARY KEY, " +
	                "METODO VARCHAR(50) NOT NULL, " +
	                "IMPORTO INT NOT NULL, " +
	                "DATA_PAGAMENTO DATE NOT NULL, " +
	                "ID_CLIENTE INT NOT NULL, " +
	                "ID_ABBONAMENTO VARCHAR(50) NOT NULL, " +
	                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE), " +
	                "FOREIGN KEY (ID_ABBONAMENTO) REFERENCES ABBONAMENTO(ID_ABBONAMENTO)" +
	                ");";

	        try (Connection conn = GestioneDB.getConnection();
	             Statement stmt = conn.createStatement()) {

	            stmt.execute(sqlCliente);
	            stmt.execute(sqlSpogliatoio);
	            stmt.execute(sqlSala);
	            stmt.execute(sqlSalaPesi);
	            stmt.execute(sqlSalaCorsi);
	            stmt.execute(sqlSpa);
	            stmt.execute(sqlCliente);
	            stmt.execute(sqlAbbonamento);
	            stmt.execute(sqlAbbonamentoBasico);
	            stmt.execute(sqlAbbonamentoCompleto);
	            stmt.execute(sqlAbbonamentoCorsi);
	            stmt.execute(sqlPagamento);

	        } catch (SQLException e) {
	            e.printStackTrace(); // qui volendo possiamo usare log4j
	        }
	    }
}
