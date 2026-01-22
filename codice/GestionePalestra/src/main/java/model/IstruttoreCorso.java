package model;

public class IstruttoreCorso extends Dipendente {

    private String tipoCorsoInsegnato; // es. "Yoga", "CrossFit", "Spinning"

    // eventualmente potrai collegare qui un oggetto Corso
    private Corso corsoPrincipale;

    // ============================
    // COSTRUTTORI
    // ============================
    public IstruttoreCorso() {
        super();
    }

    public IstruttoreCorso(String idDipendente,
                           String nome,
                           String cognome,
                           String orarioDisponibilita,
                           String tipoCorsoInsegnato) {
        super(idDipendente, nome, cognome, orarioDisponibilita);
        this.tipoCorsoInsegnato = tipoCorsoInsegnato;
    }

    // ============================
    // GETTER & SETTER
    // ============================
    public String getTipoCorsoInsegnato() {
        return tipoCorsoInsegnato;
    }

    public void setTipoCorsoInsegnato(String tipoCorsoInsegnato) {
        this.tipoCorsoInsegnato = tipoCorsoInsegnato;
    }

    public Corso getCorsoPrincipale() {
        return corsoPrincipale;
    }

    public void setCorsoPrincipale(Corso corsoPrincipale) {
        this.corsoPrincipale = corsoPrincipale;
    }

    // ============================
    // DESCRIZIONE
    // ============================
    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Ruolo: Istruttore di corsi\n");
        sb.append("Corso insegnato: ").append(tipoCorsoInsegnato).append("\n");
        return sb.toString();
    }

    public Corso consigliaCorso(Cliente cliente) {
        // placeholder logico
        return corsoPrincipale;
    }
}
