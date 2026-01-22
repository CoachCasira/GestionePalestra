package db.dao;

import db.GestioneDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO di sola lettura per mostrare la panoramica della palestra:
 * - sala pesi (info generali + elenco macchinari)
 * - SPA
 * - sale corsi
 */
public class PalestraDAO {

    // ===================== DTO =====================

    public static class SalaPesiInfo {
        public final int idSala;
        public final String orariApertura;
        public final int capienza;
        public final boolean disponibilita;
        public final int metratura;
        public final int numMacchinari;
        public final int numPanche;
        public final int numPesiLiberi;

        public SalaPesiInfo(int idSala,
                            String orariApertura,
                            int capienza,
                            boolean disponibilita,
                            int metratura,
                            int numMacchinari,
                            int numPanche,
                            int numPesiLiberi) {
            this.idSala = idSala;
            this.orariApertura = orariApertura;
            this.capienza = capienza;
            this.disponibilita = disponibilita;
            this.metratura = metratura;
            this.numMacchinari = numMacchinari;
            this.numPanche = numPanche;
            this.numPesiLiberi = numPesiLiberi;
        }
    }

    public static class MacchinarioInfo {
        public final int idMacchinario;
        public final String nome;
        public final String marca;
        public final Integer capacitaCarico;  // può essere NULL
        public final boolean occupato;

        public MacchinarioInfo(int idMacchinario,
                               String nome,
                               String marca,
                               Integer capacitaCarico,
                               boolean occupato) {
            this.idMacchinario = idMacchinario;
            this.nome = nome;
            this.marca = marca;
            this.capacitaCarico = capacitaCarico;
            this.occupato = occupato;
        }

        /** Descrizione “umana” per la view. */
        public String descrizione() {
            StringBuilder sb = new StringBuilder();
            sb.append(nome);
            if (marca != null && !marca.isEmpty()) {
                sb.append(" (").append(marca).append(")");
            }
            if (capacitaCarico != null) {
                sb.append(" – carico max: ").append(capacitaCarico).append(" kg");
            }
            sb.append(occupato ? "  [OCCUPATO]" : "  [LIBERO]");
            return sb.toString();
        }
    }

    public static class SpaInfo {
        public final int idSala;
        public final String orariApertura;
        public final int capienza;
        public final boolean disponibilita;
        public final int numSaune;
        public final int numPiscine;

        public SpaInfo(int idSala,
                       String orariApertura,
                       int capienza,
                       boolean disponibilita,
                       int numSaune,
                       int numPiscine) {
            this.idSala = idSala;
            this.orariApertura = orariApertura;
            this.capienza = capienza;
            this.disponibilita = disponibilita;
            this.numSaune = numSaune;
            this.numPiscine = numPiscine;
        }
    }

    public static class SalaCorsoInfo {
        public final int idSala;
        public final String orariApertura;
        public final int capienza;
        public final boolean disponibilita;
        public final String orarioCorso;   // SALA_CORSI.ORARIO_CORSO

        public SalaCorsoInfo(int idSala,
                             String orariApertura,
                             int capienza,
                             boolean disponibilita,
                             String orarioCorso) {
            this.idSala = idSala;
            this.orariApertura = orariApertura;
            this.capienza = capienza;
            this.disponibilita = disponibilita;
            this.orarioCorso = orarioCorso;
        }
    }

    // ===================== SALA PESI =====================

    /** Ritorna l’unica sala pesi (join SALA + SALA_PESI). */
    public static SalaPesiInfo getSalaPesiInfo() throws Exception {
        String sql =
                "SELECT s.ID_SALA, s.ORARI_APERTURA, s.CAPIENZA, s.DISPONIBILITA, " +
                "       p.METRATURA, p.NUM_MACCHINARI, p.NUM_PANCHE, p.NUM_PESI_LIBERI " +
                "FROM SALA s " +
                "JOIN SALA_PESI p ON s.ID_SALA = p.ID_SALA";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                return null;
            }

            int idSala = rs.getInt("ID_SALA");
            String orari = rs.getString("ORARI_APERTURA");
            int capienza = rs.getInt("CAPIENZA");
            boolean disp = rs.getBoolean("DISPONIBILITA");
            int metratura = rs.getInt("METRATURA");
            int numMacch = rs.getInt("NUM_MACCHINARI");
            int numPanche = rs.getInt("NUM_PANCHE");
            int numPesiLiberi = rs.getInt("NUM_PESI_LIBERI");

            return new SalaPesiInfo(
                    idSala, orari, capienza, disp,
                    metratura, numMacch, numPanche, numPesiLiberi
            );
        }
    }

    /** Elenco dei macchinari in sala pesi. */
    public static List<MacchinarioInfo> getMacchinariSalaPesi() throws Exception {
        String sql =
                "SELECT ID_MACCHINARIO, NOME, MARCA, CAPACITA_CARICO, OCCUPATO " +
                "FROM MACCHINARIO " +
                "ORDER BY NOME";

        List<MacchinarioInfo> list = new ArrayList<>();

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID_MACCHINARIO");
                String nome = rs.getString("NOME");
                String marca = rs.getString("MARCA");
                int cap = rs.getInt("CAPACITA_CARICO");
                Integer capObj = rs.wasNull() ? null : cap;
                boolean occ = rs.getBoolean("OCCUPATO");

                list.add(new MacchinarioInfo(id, nome, marca, capObj, occ));
            }
        }

        return list;
    }

    // ===================== SPA =====================

    /** Ritorna la SPA (join SALA + SPA). */
    public static SpaInfo getSpaInfo() throws Exception {
        String sql =
                "SELECT s.ID_SALA, s.ORARI_APERTURA, s.CAPIENZA, s.DISPONIBILITA, " +
                "       sp.NUM_SAUNE, sp.NUM_PISCINE " +
                "FROM SALA s " +
                "JOIN SPA sp ON s.ID_SALA = sp.ID_SALA";

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                return null;
            }

            int idSala = rs.getInt("ID_SALA");
            String orari = rs.getString("ORARI_APERTURA");
            int capienza = rs.getInt("CAPIENZA");
            boolean disp = rs.getBoolean("DISPONIBILITA");
            int numSaune = rs.getInt("NUM_SAUNE");
            int numPiscine = rs.getInt("NUM_PISCINE");

            return new SpaInfo(idSala, orari, capienza, disp, numSaune, numPiscine);
        }
    }

    // ===================== SALE CORSI =====================

    /** Ritorna le info delle sale corsi (Spinning, Pilates, AcquaGym, …). */
    public static List<SalaCorsoInfo> getSaleCorsiInfo() throws Exception {
        String sql =
                "SELECT s.ID_SALA, s.ORARI_APERTURA, s.CAPIENZA, s.DISPONIBILITA, " +
                "       c.ORARIO_CORSO " +
                "FROM SALA s " +
                "JOIN SALA_CORSI c ON s.ID_SALA = c.ID_SALA " +
                "ORDER BY s.ID_SALA";

        List<SalaCorsoInfo> list = new ArrayList<>();

        try (Connection conn = GestioneDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idSala = rs.getInt("ID_SALA");
                String orari = rs.getString("ORARI_APERTURA");
                int capienza = rs.getInt("CAPIENZA");
                boolean disp = rs.getBoolean("DISPONIBILITA");
                String orarioCorso = rs.getString("ORARIO_CORSO");

                list.add(new SalaCorsoInfo(
                        idSala, orari, capienza, disp, orarioCorso
                ));
            }
        }

        return list;
    }
}
