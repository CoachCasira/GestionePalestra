package model;

public class Spogliatoio {

    private int idSpogliatoio;
    private int numArmadiettiTotali;
    private int numDocce;
    private int numArmadiettiOccupati;

    public Spogliatoio() {
    }

    public Spogliatoio(int idSpogliatoio,
                       int numArmadiettiTotali,
                       int numDocce,
                       int numArmadiettiOccupati) {
        this.idSpogliatoio = idSpogliatoio;
        this.numArmadiettiTotali = numArmadiettiTotali;
        this.numDocce = numDocce;
        this.numArmadiettiOccupati = numArmadiettiOccupati;
    }

    public int getIdSpogliatoio() {
        return idSpogliatoio;
    }

    public void setIdSpogliatoio(int idSpogliatoio) {
        this.idSpogliatoio = idSpogliatoio;
    }

    public int getNumArmadiettiTotali() {
        return numArmadiettiTotali;
    }

    public void setNumArmadiettiTotali(int numArmadiettiTotali) {
        this.numArmadiettiTotali = numArmadiettiTotali;
    }

    public int getNumDocce() {
        return numDocce;
    }

    public void setNumDocce(int numDocce) {
        this.numDocce = numDocce;
    }

    public int getNumArmadiettiOccupati() {
        return numArmadiettiOccupati;
    }

    public void setNumArmadiettiOccupati(int numArmadiettiOccupati) {
        this.numArmadiettiOccupati = numArmadiettiOccupati;
    }

    public int getNumArmadiettiLiberi() {
        return numArmadiettiTotali - numArmadiettiOccupati;
    }

    public boolean verificaArmadiettiLiberi() {
        return getNumArmadiettiLiberi() > 0;
    }

    public void assegnaArmadietto() {
        if (verificaArmadiettiLiberi()) {
            numArmadiettiOccupati++;
        }
    }

    public void liberaArmadietto() {
        if (numArmadiettiOccupati > 0) {
            numArmadiettiOccupati--;
        }
    }
}
