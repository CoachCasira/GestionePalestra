package model;

public class Nutrizionista extends Dipendente {

    private String parcella;      // es. "60 €/consulenza"
    private String specializzazione; // es. "Sportiva", "Clinica"

    // ============================
    // COSTRUTTORI
    // ============================
    public Nutrizionista() {
        super();
    }

    public Nutrizionista(String idDipendente,
                         String nome,
                         String cognome,
                         String orarioDisponibilita,
                         String parcella,
                         String specializzazione) {
        super(idDipendente, nome, cognome, orarioDisponibilita);
        this.parcella = parcella;
        this.specializzazione = specializzazione;
    }

    // ============================
    // GETTER & SETTER
    // ============================
    public String getParcella() {
        return parcella;
    }

    public void setParcella(String parcella) {
        this.parcella = parcella;
    }

    public String getSpecializzazione() {
        return specializzazione;
    }

    public void setSpecializzazione(String specializzazione) {
        this.specializzazione = specializzazione;
    }

    // ============================
    // DESCRIZIONE
    // ============================
    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Ruolo: Nutrizionista\n");
        sb.append("Specializzazione: ").append(specializzazione).append("\n");
        sb.append("Parcella indicativa: ").append(parcella).append("\n");
        return sb.toString();
    }

    public String creaPianoAlimentare() {
        // placeholder logico
        return "Piano alimentare creato da " + getNomeCompleto();
    }
}
