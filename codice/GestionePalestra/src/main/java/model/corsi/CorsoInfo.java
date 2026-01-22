package model.corsi;

/**
 * DTO per l'informazione di un corso.
 */
public class CorsoInfo {

    public final int idCorso;
    public final String nome;
    public final String descrizione;
    public final int durataMinuti;

    public CorsoInfo(int idCorso, String nome, String descrizione, int durataMinuti) {
        this.idCorso = idCorso;
        this.nome = nome;
        this.descrizione = descrizione;
        this.durataMinuti = durataMinuti;
    }
}
