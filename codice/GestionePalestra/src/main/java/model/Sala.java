package model;

public abstract class Sala {

    private int idSala;
    private String nome;              // es. "Sala Pesi 1", "Sala Corsi A"
    private String orariApertura;     // es. "Lun-Dom 7:00-22:00"
    private int capienza;             // persone
    private boolean disponibile;      // in manutenzione / attiva

    // ============================
    // COSTRUTTORI
    // ============================
    protected Sala() {
    }

    protected Sala(int idSala,
                   String nome,
                   String orariApertura,
                   int capienza,
                   boolean disponibile) {
        this.idSala = idSala;
        this.nome = nome;
        this.orariApertura = orariApertura;
        this.capienza = capienza;
        this.disponibile = disponibile;
    }

    // ============================
    // GETTER & SETTER
    // ============================
    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getOrariApertura() {
        return orariApertura;
    }

    public void setOrariApertura(String orariApertura) {
        this.orariApertura = orariApertura;
    }

    public int getCapienza() {
        return capienza;
    }

    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public boolean verificaDisponibilita() {
        return disponibile;
    }

    public void prenotaSala() {
        // potresti mettere logica tipo: diminuisci posti liberi ecc.
    }

    public void liberaSala() {
        // logica inversa
    }

    public String getDescrizioneBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sala: ").append(nome).append("\n");
        sb.append("ID sala: ").append(idSala).append("\n");
        sb.append("Orari apertura: ").append(orariApertura).append("\n");
        sb.append("Capienza: ").append(capienza).append(" persone\n");
        sb.append("Stato: ").append(disponibile ? "Disponibile" : "Non disponibile").append("\n");
        return sb.toString();
    }

    public abstract String getDescrizioneCompleta();
}
