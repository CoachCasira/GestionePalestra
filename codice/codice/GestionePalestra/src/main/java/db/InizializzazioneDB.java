package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class InizializzazioneDB {

    public static void init() {

        // 1 - SPOGLIATOIO
        String sqlSpogliatoio = "CREATE TABLE IF NOT EXISTS SPOGLIATOIO (" +
                "ID_SPOGLIATOIO INT AUTO_INCREMENT PRIMARY KEY, " +
                "NUM_ARMADIETTI INT NOT NULL, " +
                "NUM_DOCCE INT NOT NULL, " +
                "NUM_ARMADIETTI_LIBERI INT NOT NULL" +
                ");";

        // 2 - SALA (padre)
        String sqlSala = "CREATE TABLE IF NOT EXISTS SALA (" +
                "ID_SALA INT AUTO_INCREMENT PRIMARY KEY, " +
                "ORARI_APERTURA VARCHAR(100) NOT NULL, " +
                "CAPIENZA INT NOT NULL, " +
                "DISPONIBILITA BOOLEAN NOT NULL" +
                ");";

        // 3 - SALA_PESI (figlia)
        String sqlSalaPesi = "CREATE TABLE IF NOT EXISTS SALA_PESI (" +
                "ID_SALA INT PRIMARY KEY, " +
                "METRATURA INT NOT NULL, " +
                "NUM_MACCHINARI INT NOT NULL, " +
                "NUM_PANCHE INT NOT NULL, " +
                "NUM_PESI_LIBERI INT NOT NULL, " +
                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
                ");";

        // 4 - SALA_CORSI (figlia)
        String sqlSalaCorsi = "CREATE TABLE IF NOT EXISTS SALA_CORSI (" +
                "ID_SALA INT PRIMARY KEY, " +
                "ORARIO_CORSO VARCHAR(100) NOT NULL, " +
                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
                ");";

        // 5 - SPA (figlia di Sala)
        String sqlSpa = "CREATE TABLE IF NOT EXISTS SPA (" +
                "ID_SALA INT PRIMARY KEY, " +
                "NUM_SAUNE INT NOT NULL, " +
                "NUM_PISCINE INT NOT NULL, " +
                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
                ");";

        // 6 - CLIENTE (con EMAIL)
        String sqlCliente = "CREATE TABLE IF NOT EXISTS CLIENTE (" +
                "ID_CLIENTE INT AUTO_INCREMENT PRIMARY KEY, " +
                "USERNAME VARCHAR(50) NOT NULL UNIQUE, " +
                "EMAIL VARCHAR(100) NOT NULL UNIQUE, " +
                "NOME VARCHAR(100) NOT NULL, " +
                "COGNOME VARCHAR(100) NOT NULL, " +
                "CF VARCHAR(16) NOT NULL UNIQUE, " +
                "LUOGO_NASCITA VARCHAR(100) NOT NULL, " +
                "DATA_NASCITA DATE NOT NULL, " +
                "IBAN VARCHAR(34), " +
                "PASSWORD VARCHAR(100) NOT NULL" +
                ");";

        // 6bis - PASSWORD_RESET_TOKEN
        String sqlPasswordReset = "CREATE TABLE IF NOT EXISTS PASSWORD_RESET_TOKEN (" +
                "ID_TOKEN INT AUTO_INCREMENT PRIMARY KEY, " +
                "ID_CLIENTE INT NOT NULL, " +
                "TOKEN VARCHAR(20) NOT NULL, " +
                "SCADENZA TIMESTAMP NOT NULL, " +
                "UTILIZZATO BOOLEAN NOT NULL DEFAULT FALSE, " +
                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE)" +
                ");";

        // 7 - ABBONAMENTO (padre)
        String sqlAbbonamento = "CREATE TABLE IF NOT EXISTS ABBONAMENTO (" +
                "ID_ABBONAMENTO INT AUTO_INCREMENT PRIMARY KEY, " +
                "TIPO VARCHAR(20) NOT NULL, " +
                "SCADENZA DATE, " +
                "ID_SPOGLIATOIO INT, " +
                "ID_CLIENTE INT NOT NULL, " +
                "FASCIA_ORARIA_CONSENTITA VARCHAR(100), " +
                "PREZZO INT NOT NULL, " +
                "ATTIVO BOOLEAN DEFAULT TRUE, " +
                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE), " +
                "FOREIGN KEY (ID_SPOGLIATOIO) REFERENCES SPOGLIATOIO(ID_SPOGLIATOIO)" +
                ");";

        // 8 - SOTTOCLASSI ABBONAMENTO
        String sqlAbbonamentoBasico = "CREATE TABLE IF NOT EXISTS ABBONAMENTO_BASICO (" +
                "ID_ABBONAMENTO INT PRIMARY KEY, " +
                "ID_SALA_PESI INT NOT NULL, " +
                "LIMITE_INGRESSI_MENSILI INT NOT NULL, " +
                "FOREIGN KEY (ID_ABBONAMENTO) REFERENCES ABBONAMENTO(ID_ABBONAMENTO), " +
                "FOREIGN KEY (ID_SALA_PESI) REFERENCES SALA_PESI(ID_SALA)" +
                ");";

        String sqlAbbonamentoCompleto = "CREATE TABLE IF NOT EXISTS ABBONAMENTO_COMPLETO (" +
                "ID_ABBONAMENTO INT PRIMARY KEY, " +
                "ID_SALA INT NOT NULL, " +
                "SOGLIA_SCONTO INT NOT NULL, " +
                "FOREIGN KEY (ID_ABBONAMENTO) REFERENCES ABBONAMENTO(ID_ABBONAMENTO), " +
                "FOREIGN KEY (ID_SALA) REFERENCES SALA(ID_SALA)" +
                ");";

        String sqlAbbonamentoCorsi = "CREATE TABLE IF NOT EXISTS ABBONAMENTO_CORSI (" +
                "ID_ABBONAMENTO INT PRIMARY KEY, " +
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
                "ID_ABBONAMENTO INT NOT NULL, " +
                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE), " +
                "FOREIGN KEY (ID_ABBONAMENTO) REFERENCES ABBONAMENTO(ID_ABBONAMENTO)" +
                ");";

        // 10 - DIPENDENTE (padre)
        String sqlDipendente = "CREATE TABLE IF NOT EXISTS DIPENDENTE (" +
                "ID_DIPENDENTE INT AUTO_INCREMENT PRIMARY KEY, " +
                "NOME VARCHAR(100) NOT NULL, " +
                "COGNOME VARCHAR(100) NOT NULL, " +
                "RUOLO VARCHAR(30) NOT NULL, " +
                "ORARIO_DISP VARCHAR(100)" +
                ");";

        // 11 - CORSO
        String sqlCorso = "CREATE TABLE IF NOT EXISTS CORSO (" +
                "ID_CORSO INT AUTO_INCREMENT PRIMARY KEY, " +
                "NOME VARCHAR(100) NOT NULL, " +
                "DESCRIZIONE VARCHAR(500) NOT NULL, " +
                "DURATA_MINUTI INT NOT NULL" +
                ");";

        // 12 - LEZIONE_CORSO
        String sqlLezioneCorso = "CREATE TABLE IF NOT EXISTS LEZIONE_CORSO (" +
                "ID_LEZIONE INT AUTO_INCREMENT PRIMARY KEY, " +
                "ID_CORSO INT NOT NULL, " +
                "DATA_LEZIONE DATE NOT NULL, " +
                "ORA_LEZIONE TIME NOT NULL, " +
                "POSTI_TOTALI INT NOT NULL, " +
                "POSTI_PRENOTATI INT NOT NULL DEFAULT 0, " +
                "ID_ISTRUTTORE INT NOT NULL, " +
                "FOREIGN KEY (ID_CORSO) REFERENCES CORSO(ID_CORSO), " +
                "FOREIGN KEY (ID_ISTRUTTORE) REFERENCES DIPENDENTE(ID_DIPENDENTE)" +
                ");";

        // 13 - ISCRIZIONE_CORSO
        String sqlIscrizioneCorso = "CREATE TABLE IF NOT EXISTS ISCRIZIONE_CORSO (" +
                "ID_ISCRIZIONE INT AUTO_INCREMENT PRIMARY KEY, " +
                "ID_CLIENTE INT NOT NULL, " +
                "ID_LEZIONE INT NOT NULL, " +
                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE), " +
                "FOREIGN KEY (ID_LEZIONE) REFERENCES LEZIONE_CORSO(ID_LEZIONE)" +
                ");";

        // 14 - CONSULENZA
        String sqlConsulenza = "CREATE TABLE IF NOT EXISTS CONSULENZA (" +
                "ID_CONSULENZA INT AUTO_INCREMENT PRIMARY KEY, " +
                "ID_CLIENTE INT NOT NULL, " +
                "ID_DIPENDENTE INT NOT NULL, " +
                "TIPO VARCHAR(30) NOT NULL, " +
                "DATA_CONSULENZA DATE NOT NULL, " +
                "ORA_CONSULENZA TIME NOT NULL, " +
                "NOTE VARCHAR(255), " +
                "FOREIGN KEY (ID_CLIENTE) REFERENCES CLIENTE(ID_CLIENTE), " +
                "FOREIGN KEY (ID_DIPENDENTE) REFERENCES DIPENDENTE(ID_DIPENDENTE)" +
                ");";

        // 15 - PERSONAL_TRAINER
        String sqlPersonalTrainer = "CREATE TABLE IF NOT EXISTS PERSONAL_TRAINER (" +
                "ID_DIPENDENTE INT PRIMARY KEY, " +
                "PARTITA_IVA VARCHAR(20), " +
                "ANNI_ESPERIENZA INT, " +
                "CERTIFICATI VARCHAR(255), " +
                "FOREIGN KEY (ID_DIPENDENTE) REFERENCES DIPENDENTE(ID_DIPENDENTE)" +
                ");";

        // 16 - ISTRUTTORE_CORSO
        String sqlIstruttoreCorso = "CREATE TABLE IF NOT EXISTS ISTRUTTORE_CORSO (" +
                "ID_DIPENDENTE INT PRIMARY KEY, " +
                "TIPO_CORSO_INSEGNATO VARCHAR(100) NOT NULL, " +
                "FOREIGN KEY (ID_DIPENDENTE) REFERENCES DIPENDENTE(ID_DIPENDENTE)" +
                ");";

        // 17 - NUTRIZIONISTA
        String sqlNutrizionista = "CREATE TABLE IF NOT EXISTS NUTRIZIONISTA (" +
                "ID_DIPENDENTE INT PRIMARY KEY, " +
                "PARCELLA VARCHAR(50), " +
                "FOREIGN KEY (ID_DIPENDENTE) REFERENCES DIPENDENTE(ID_DIPENDENTE)" +
                ");";

        // 18 - MACCHINARIO
        String sqlMacchinario = "CREATE TABLE IF NOT EXISTS MACCHINARIO (" +
                "ID_MACCHINARIO INT AUTO_INCREMENT PRIMARY KEY, " +
                "NOME VARCHAR(100) NOT NULL, " +
                "MARCA VARCHAR(100), " +
                "CAPACITA_CARICO INT, " +
                "OCCUPATO BOOLEAN NOT NULL, " +
                "ID_SALA_PESI INT, " +
                "FOREIGN KEY (ID_SALA_PESI) REFERENCES SALA_PESI(ID_SALA)" +
                ");";

        try (Connection conn = GestioneDB.getConnection();
             Statement stmt = conn.createStatement()) {

            // CREAZIONE TABELLE
            stmt.execute(sqlSpogliatoio);
            stmt.execute(sqlSala);
            stmt.execute(sqlSalaPesi);
            stmt.execute(sqlSalaCorsi);
            stmt.execute(sqlSpa);
            stmt.execute(sqlCliente);
            stmt.execute(sqlPasswordReset);
            stmt.execute(sqlAbbonamento);
            stmt.execute(sqlAbbonamentoBasico);
            stmt.execute(sqlAbbonamentoCompleto);
            stmt.execute(sqlAbbonamentoCorsi);
            stmt.execute(sqlPagamento);

            stmt.execute(sqlDipendente);
            stmt.execute(sqlCorso);
            stmt.execute(sqlLezioneCorso);
            stmt.execute(sqlIscrizioneCorso);

            stmt.execute(sqlConsulenza);
            stmt.execute(sqlPersonalTrainer);
            stmt.execute(sqlIstruttoreCorso);
            stmt.execute(sqlNutrizionista);
            stmt.execute(sqlMacchinario);

            // POPOLAMENTO
            popolaDipendenti(conn);
            popolaSaleESpa(conn);
            popolaSpogliatoi(conn);
            popolaMacchinari(conn);
            popolaCorsiELezioni(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- metodi di popolamento come li avevi, li lascio uguali ---

    private static void popolaDipendenti(Connection conn) throws SQLException {
        if (!isTableEmpty(conn, "DIPENDENTE")) return;

        String sqlIns = "INSERT INTO DIPENDENTE (NOME, COGNOME, RUOLO, ORARIO_DISP) " +
                        "VALUES (?, ?, ?, ?)";

        try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {

            int idPt1 = inserisciDip(ps, "Luca",   "Rossi",  "PERSONAL_TRAINER", "Lun-Ven 9:00-13:00");
            int idPt2 = inserisciDip(ps, "Sara",   "Bianchi","PERSONAL_TRAINER", "Lun-Ven 14:00-18:00");
            int idPt3 = inserisciDip(ps, "Marco",  "Verdi",  "PERSONAL_TRAINER", "Sab 9:00-13:00");

            int idNu1 = inserisciDip(ps, "Giulia", "Conti",  "NUTRIZIONISTA", "Mar-Gio 10:00-16:00");
            int idNu2 = inserisciDip(ps, "Paolo",  "Fumagalli","NUTRIZIONISTA", "Lun-Mer 15:00-19:00");
            int idNu3 = inserisciDip(ps, "Anna",   "Marino", "NUTRIZIONISTA", "Sab 9:00-12:00");

            int idIs1 = inserisciDip(ps, "Lorenzo","Neri",   "ISTRUTTORE_CORSO", "Corsi serali");
            int idIs2 = inserisciDip(ps, "Chiara", "Riva",   "ISTRUTTORE_CORSO", "Corsi pomeridiani");
            int idIs3 = inserisciDip(ps, "Davide", "Sala",   "ISTRUTTORE_CORSO", "Corsi mattutini");

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("INSERT INTO PERSONAL_TRAINER (ID_DIPENDENTE, PARTITA_IVA, ANNI_ESPERIENZA, CERTIFICATI) VALUES " +
                        "(" + idPt1 + ", 'IT12345678901', 5, 'FIPE, ISSA')");
                st.executeUpdate("INSERT INTO PERSONAL_TRAINER (ID_DIPENDENTE, PARTITA_IVA, ANNI_ESPERIENZA, CERTIFICATI) VALUES " +
                        "(" + idPt2 + ", 'IT23456789012', 3, 'NASM')");
                st.executeUpdate("INSERT INTO PERSONAL_TRAINER (ID_DIPENDENTE, PARTITA_IVA, ANNI_ESPERIENZA, CERTIFICATI) VALUES " +
                        "(" + idPt3 + ", 'IT34567890123', 7, 'FIPE, CrossFit L1')");

                st.executeUpdate("INSERT INTO NUTRIZIONISTA (ID_DIPENDENTE, PARCELLA) VALUES (" + idNu1 + ", '60 €/ora')");
                st.executeUpdate("INSERT INTO NUTRIZIONISTA (ID_DIPENDENTE, PARCELLA) VALUES (" + idNu2 + ", '70 €/ora')");
                st.executeUpdate("INSERT INTO NUTRIZIONISTA (ID_DIPENDENTE, PARCELLA) VALUES (" + idNu3 + ", '80 €/ora')");

                st.executeUpdate("INSERT INTO ISTRUTTORE_CORSO (ID_DIPENDENTE, TIPO_CORSO_INSEGNATO) VALUES (" + idIs1 + ", 'Spinning')");
                st.executeUpdate("INSERT INTO ISTRUTTORE_CORSO (ID_DIPENDENTE, TIPO_CORSO_INSEGNATO) VALUES (" + idIs2 + ", 'Pilates')");
                st.executeUpdate("INSERT INTO ISTRUTTORE_CORSO (ID_DIPENDENTE, TIPO_CORSO_INSEGNATO) VALUES (" + idIs3 + ", 'AcquaGym')");
            }
        }
    }

    private static void popolaSaleESpa(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            if (isTableEmpty(conn, "SALA")) {
                stmt.executeUpdate(
                        "INSERT INTO SALA (ORARI_APERTURA, CAPIENZA, DISPONIBILITA) VALUES " +
                                "('Lun-Dom 7:00-22:00', 60, TRUE)");
                stmt.executeUpdate(
                        "INSERT INTO SALA (ORARI_APERTURA, CAPIENZA, DISPONIBILITA) VALUES " +
                                "('Lun-Mer-Ven 18:00-19:00', 25, TRUE)");
                stmt.executeUpdate(
                        "INSERT INTO SALA (ORARI_APERTURA, CAPIENZA, DISPONIBILITA) VALUES " +
                                "('Mar-Gio 19:00-20:00', 20, TRUE)");
                stmt.executeUpdate(
                        "INSERT INTO SALA (ORARI_APERTURA, CAPIENZA, DISPONIBILITA) VALUES " +
                                "('Sab 10:00-11:00', 15, TRUE)");
                stmt.executeUpdate(
                        "INSERT INTO SALA (ORARI_APERTURA, CAPIENZA, DISPONIBILITA) VALUES " +
                                "('Lun-Dom 10:00-21:00', 30, TRUE)");
            }

            if (isTableEmpty(conn, "SALA_PESI")) {
                stmt.executeUpdate(
                        "INSERT INTO SALA_PESI (ID_SALA, METRATURA, NUM_MACCHINARI, NUM_PANCHE, NUM_PESI_LIBERI) " +
                                "VALUES (1, 200, 30, 10, 50)");
            }

            if (isTableEmpty(conn, "SALA_CORSI")) {
                stmt.executeUpdate(
                        "INSERT INTO SALA_CORSI (ID_SALA, ORARIO_CORSO) " +
                                "VALUES (2, 'Spinning - Lun-Mer-Ven 18:00')");
                stmt.executeUpdate(
                        "INSERT INTO SALA_CORSI (ID_SALA, ORARIO_CORSO) " +
                                "VALUES (3, 'Pilates - Mar-Gio 19:00')");
                stmt.executeUpdate(
                        "INSERT INTO SALA_CORSI (ID_SALA, ORARIO_CORSO) " +
                                "VALUES (4, 'AcquaGym - Sab 10:00')");
            }

            if (isTableEmpty(conn, "SPA")) {
                stmt.executeUpdate(
                        "INSERT INTO SPA (ID_SALA, NUM_SAUNE, NUM_PISCINE) " +
                                "VALUES (5, 2, 1)");
            }
        }
    }

    private static void popolaSpogliatoi(Connection conn) throws SQLException {
        if (isTableEmpty(conn, "SPOGLIATOIO")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "INSERT INTO SPOGLIATOIO (NUM_ARMADIETTI, NUM_DOCCE, NUM_ARMADIETTI_LIBERI) " +
                                "VALUES (50, 6, 50)");
                stmt.executeUpdate(
                        "INSERT INTO SPOGLIATOIO (NUM_ARMADIETTI, NUM_DOCCE, NUM_ARMADIETTI_LIBERI) " +
                                "VALUES (50, 6, 50)");
            }
        }
    }

    private static void popolaMacchinari(Connection conn) throws SQLException {
        if (isTableEmpty(conn, "MACCHINARIO")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "INSERT INTO MACCHINARIO (NOME, MARCA, CAPACITA_CARICO, OCCUPATO, ID_SALA_PESI) VALUES " +
                                "('Lat Machine', 'Technogym', 120, FALSE, 1)");
                stmt.executeUpdate(
                        "INSERT INTO MACCHINARIO (NOME, MARCA, CAPACITA_CARICO, OCCUPATO, ID_SALA_PESI) VALUES " +
                                "('Panca piana', 'Panatta', 200, FALSE, 1)");
                stmt.executeUpdate(
                        "INSERT INTO MACCHINARIO (NOME, MARCA, CAPACITA_CARICO, OCCUPATO, ID_SALA_PESI) VALUES " +
                                "('Tapis Roulant', 'Technogym', 150, FALSE, 1)");
            }
        }
    }

    // --- corsi e lezioni (come avevi già) ---

    private static void popolaCorsiELezioni(Connection conn) throws SQLException {
        if (!isTableEmpty(conn, "CORSO")) return;

        String sqlInsCorso =
                "INSERT INTO CORSO (NOME, DESCRIZIONE, DURATA_MINUTI) VALUES (?, ?, ?)";

        int idSpinning = -1;
        int idPilates  = -1;
        int idAcquaGym = -1;

        try (java.sql.PreparedStatement ps =
                     conn.prepareStatement(sqlInsCorso, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, "Spinning");
            ps.setString(2, "Allenamento cardiovascolare su bike indoor ad alta intensità.");
            ps.setInt(3, 60);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idSpinning = rs.getInt(1);
            }

            ps.setString(1, "Pilates");
            ps.setString(2, "Corso di tonificazione e postura basato su esercizi a corpo libero.");
            ps.setInt(3, 60);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idPilates = rs.getInt(1);
            }

            ps.setString(1, "AcquaGym");
            ps.setString(2, "Allenamento aerobico in acqua a basso impatto articolare.");
            ps.setInt(3, 60);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idAcquaGym = rs.getInt(1);
            }
        }

        if (isTableEmpty(conn, "LEZIONE_CORSO")) {

            int istr1 = -1, istr2 = -1, istr3 = -1;
            String sqlIstr = "SELECT ID_DIPENDENTE FROM DIPENDENTE " +
                    "WHERE RUOLO = 'ISTRUTTORE_CORSO' ORDER BY ID_DIPENDENTE";

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlIstr)) {
                if (rs.next()) istr1 = rs.getInt(1);
                if (rs.next()) istr2 = rs.getInt(1);
                if (rs.next()) istr3 = rs.getInt(1);
            }

            int fallback = istr1 != -1 ? istr1 : (istr2 != -1 ? istr2 : istr3);

            String sqlInsLez =
                    "INSERT INTO LEZIONE_CORSO " +
                            "(ID_CORSO, DATA_LEZIONE, ORA_LEZIONE, POSTI_TOTALI, POSTI_PRENOTATI, ID_ISTRUTTORE) " +
                            "VALUES (?, ?, ?, ?, 0, ?)";

            try (java.sql.PreparedStatement psL = conn.prepareStatement(sqlInsLez)) {

                java.time.LocalDate oggi = java.time.LocalDate.now();

                if (idSpinning > 0) {
                    java.time.LocalDate lun = next(oggi, java.time.DayOfWeek.MONDAY);
                    java.time.LocalDate mer = next(oggi, java.time.DayOfWeek.WEDNESDAY);
                    java.time.LocalDate ven = next(oggi, java.time.DayOfWeek.FRIDAY);

                    psL.setInt(1, idSpinning);
                    psL.setDate(2, java.sql.Date.valueOf(lun));
                    psL.setTime(3, java.sql.Time.valueOf("18:00:00"));
                    psL.setInt(4, 25);
                    psL.setInt(5, istr1 != -1 ? istr1 : fallback);
                    psL.executeUpdate();

                    psL.setInt(1, idSpinning);
                    psL.setDate(2, java.sql.Date.valueOf(mer));
                    psL.setTime(3, java.sql.Time.valueOf("18:00:00"));
                    psL.setInt(4, 25);
                    psL.setInt(5, istr1 != -1 ? istr1 : fallback);
                    psL.executeUpdate();

                    psL.setInt(1, idSpinning);
                    psL.setDate(2, java.sql.Date.valueOf(ven));
                    psL.setTime(3, java.sql.Time.valueOf("18:00:00"));
                    psL.setInt(4, 25);
                    psL.setInt(5, istr1 != -1 ? istr1 : fallback);
                    psL.executeUpdate();
                }

                if (idPilates > 0) {
                    java.time.LocalDate mar = next(oggi, java.time.DayOfWeek.TUESDAY);
                    java.time.LocalDate gio = next(oggi, java.time.DayOfWeek.THURSDAY);

                    psL.setInt(1, idPilates);
                    psL.setDate(2, java.sql.Date.valueOf(mar));
                    psL.setTime(3, java.sql.Time.valueOf("19:00:00"));
                    psL.setInt(4, 20);
                    psL.setInt(5, istr2 != -1 ? istr2 : fallback);
                    psL.executeUpdate();

                    psL.setInt(1, idPilates);
                    psL.setDate(2, java.sql.Date.valueOf(gio));
                    psL.setTime(3, java.sql.Time.valueOf("19:00:00"));
                    psL.setInt(4, 20);
                    psL.setInt(5, istr2 != -1 ? istr2 : fallback);
                    psL.executeUpdate();
                }

                if (idAcquaGym > 0) {
                    java.time.LocalDate sab = next(oggi, java.time.DayOfWeek.SATURDAY);

                    psL.setInt(1, idAcquaGym);
                    psL.setDate(2, java.sql.Date.valueOf(sab));
                    psL.setTime(3, java.sql.Time.valueOf("10:00:00"));
                    psL.setInt(4, 15);
                    psL.setInt(5, istr3 != -1 ? istr3 : fallback);
                    psL.executeUpdate();
                }
            }
        }
    }

    private static int inserisciDip(java.sql.PreparedStatement ps,
                                    String nome, String cognome,
                                    String ruolo, String orario) throws SQLException {
        ps.setString(1, nome);
        ps.setString(2, cognome);
        ps.setString(3, ruolo);
        ps.setString(4, orario);
        ps.executeUpdate();
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    private static boolean isTableEmpty(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true;
    }

    private static java.time.LocalDate next(java.time.LocalDate from,
                                            java.time.DayOfWeek target) {
        int diff = target.getValue() - from.getDayOfWeek().getValue();
        if (diff <= 0) diff += 7;
        return from.plusDays(diff);
    }
}
