package model;

public class Spa extends Sala {

    private int numSaune;
    private int numPiscine;

    public Spa() {
        super();
    }

    public Spa(int idSala,
               String nome,
               String orariApertura,
               int capienza,
               boolean disponibile,
               int numSaune,
               int numPiscine) {
        super(idSala, nome, orariApertura, capienza, disponibile);
        this.numSaune = numSaune;
        this.numPiscine = numPiscine;
    }

    public int getNumSaune() {
        return numSaune;
    }

    public void setNumSaune(int numSaune) {
        this.numSaune = numSaune;
    }

    public int getNumPiscine() {
        return numPiscine;
    }

    public void setNumPiscine(int numPiscine) {
        this.numPiscine = numPiscine;
    }

    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Tipo sala: SPA\n");
        sb.append("Numero saune: ").append(numSaune).append("\n");
        sb.append("Numero piscine: ").append(numPiscine).append("\n");
        return sb.toString();
    }
}
