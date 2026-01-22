package model;

public class Macchinario {

    private int idMacchinario;
    private String nome;          // es. "Lat Machine"
    private String marca;         // es. "Technogym"
    private int capacitaCarico;   // kg massimi
    private boolean occupato;

    public Macchinario() {
    }

    public Macchinario(int idMacchinario,
                       String nome,
                       String marca,
                       int capacitaCarico,
                       boolean occupato) {
        this.idMacchinario = idMacchinario;
        this.nome = nome;
        this.marca = marca;
        this.capacitaCarico = capacitaCarico;
        this.occupato = occupato;
    }

    public int getIdMacchinario() {
        return idMacchinario;
    }

    public void setIdMacchinario(int idMacchinario) {
        this.idMacchinario = idMacchinario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getCapacitaCarico() {
        return capacitaCarico;
    }

    public void setCapacitaCarico(int capacitaCarico) {
        this.capacitaCarico = capacitaCarico;
    }

    public boolean isOccupato() {
        return occupato;
    }

    public void setOccupato(boolean occupato) {
        this.occupato = occupato;
    }

    public void occupa() {
        this.occupato = true;
    }

    public void libera() {
        this.occupato = false;
    }
}
