package model;

/**
 * Rappresenta un corso a catalogo della palestra.
 * È allineato alla tabella CORSO:
 *  - ID_CORSO
 *  - NOME
 *  - DESCRIZIONE
 *  - DURATA_MINUTI
 */
public class Corso {

    private int idCorso;
    private String nome;
    private String descrizione;
    private int durataMinuti;         // durata di una lezione tipo, in minuti

    public Corso() {
    }

    public Corso(int idCorso, String nome, String descrizione, int durataMinuti) {
        this.idCorso = idCorso;
        this.nome = nome;
        this.descrizione = descrizione;
        this.durataMinuti = durataMinuti;
    }

    public int getIdCorso() {
        return idCorso;
    }

    public void setIdCorso(int idCorso) {
        this.idCorso = idCorso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getDurataMinuti() {
        return durataMinuti;
    }

    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    @Override
    public String toString() {
        return "Corso{" +
                "idCorso=" + idCorso +
                ", nome='" + nome + '\'' +
                ", durataMinuti=" + durataMinuti +
                '}';
    }
}
