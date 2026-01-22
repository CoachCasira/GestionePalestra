package model;

public class PersonalTrainer extends Dipendente {

    private String partitaIva;
    private int anniEsperienza;
    private String certificazioni; // es. "FIPE, ISSA, CONI"

    // ============================
    // COSTRUTTORI
    // ============================
    public PersonalTrainer() {
        super();
    }

    public PersonalTrainer(String idDipendente,
                           String nome,
                           String cognome,
                           String orarioDisponibilita,
                           String partitaIva,
                           int anniEsperienza,
                           String certificazioni) {
        super(idDipendente, nome, cognome, orarioDisponibilita);
        this.partitaIva = partitaIva;
        this.anniEsperienza = anniEsperienza;
        this.certificazioni = certificazioni;
    }

    // ============================
    // GETTER & SETTER
    // ============================
    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public int getAnniEsperienza() {
        return anniEsperienza;
    }

    public void setAnniEsperienza(int anniEsperienza) {
        this.anniEsperienza = anniEsperienza;
    }

    public String getCertificazioni() {
        return certificazioni;
    }

    public void setCertificazioni(String certificazioni) {
        this.certificazioni = certificazioni;
    }

    // ============================
    // DESCRIZIONE
    // ============================
    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Ruolo: Personal Trainer\n");
        sb.append("Anni di esperienza: ").append(anniEsperienza).append("\n");
        sb.append("Certificazioni: ").append(certificazioni).append("\n");
        return sb.toString();
    }

    public String creaSchedaAllenamento() {
        // placeholder logico
        return "Scheda di allenamento creata da " + getNomeCompleto();
    }
}
