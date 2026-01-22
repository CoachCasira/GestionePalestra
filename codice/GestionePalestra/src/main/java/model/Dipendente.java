package model;

public abstract class Dipendente {

    private String idDipendente;        // es. "PT001", "NUTR003", ...
    private String nome;
    private String cognome;
    private String orarioDisponibilita; // es. "Lun-Ven 9:00-18:00"

    // ============================
    // COSTRUTTORI
    // ============================
    protected Dipendente() {
    }

    protected Dipendente(String idDipendente,
                         String nome,
                         String cognome,
                         String orarioDisponibilita) {
        this.idDipendente = idDipendente;
        this.nome = nome;
        this.cognome = cognome;
        this.orarioDisponibilita = orarioDisponibilita;
    }

    // ============================
    // GETTER & SETTER
    // ============================
    public String getIdDipendente() {
        return idDipendente;
    }

    public void setIdDipendente(String idDipendente) {
        this.idDipendente = idDipendente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getOrarioDisponibilita() {
        return orarioDisponibilita;
    }

    public void setOrarioDisponibilita(String orarioDisponibilita) {
        this.orarioDisponibilita = orarioDisponibilita;
    }

    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    /**
     * Descrizione base comune a tutti i dipendenti.
     */
    public String getDescrizioneBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dipendente: ").append(getNomeCompleto()).append("\n");
        sb.append("ID: ").append(idDipendente).append("\n");
        sb.append("Disponibilità: ").append(orarioDisponibilita).append("\n");
        return sb.toString();
    }

    /**
     * Ogni sottoclasse aggiunge le proprie info specifiche.
     */
    public abstract String getDescrizioneCompleta();

    // eventuali metodi comuni (agenda, ecc.) li aggiungeremo in futuro
}
