package model;

public class SalaPesi extends Sala {

    private int metratura;       // m^2
    private int numMacchinari;
    private int numPanche;
    private int numPesiLiberi;

    public SalaPesi() {
        super();
    }

    public SalaPesi(int idSala,
                    String nome,
                    String orariApertura,
                    int capienza,
                    boolean disponibile,
                    int metratura,
                    int numMacchinari,
                    int numPanche,
                    int numPesiLiberi) {
        super(idSala, nome, orariApertura, capienza, disponibile);
        this.metratura = metratura;
        this.numMacchinari = numMacchinari;
        this.numPanche = numPanche;
        this.numPesiLiberi = numPesiLiberi;
    }

    public int getMetratura() {
        return metratura;
    }

    public void setMetratura(int metratura) {
        this.metratura = metratura;
    }

    public int getNumMacchinari() {
        return numMacchinari;
    }

    public void setNumMacchinari(int numMacchinari) {
        this.numMacchinari = numMacchinari;
    }

    public int getNumPanche() {
        return numPanche;
    }

    public void setNumPanche(int numPanche) {
        this.numPanche = numPanche;
    }

    public int getNumPesiLiberi() {
        return numPesiLiberi;
    }

    public void setNumPesiLiberi(int numPesiLiberi) {
        this.numPesiLiberi = numPesiLiberi;
    }

    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Tipo sala: Sala Pesi\n");
        sb.append("Metratura: ").append(metratura).append(" m²\n");
        sb.append("Macchinari: ").append(numMacchinari).append("\n");
        sb.append("Panche: ").append(numPanche).append("\n");
        sb.append("Pesi liberi: ").append(numPesiLiberi).append("\n");
        return sb.toString();
    }
}
