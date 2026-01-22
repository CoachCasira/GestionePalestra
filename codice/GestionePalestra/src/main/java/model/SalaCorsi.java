package model;

public class SalaCorsi extends Sala {

    private String orarioCorso;  // es. "Lun-Mer-Ven 19:00-20:00"

    public SalaCorsi() {
        super();
    }

    public SalaCorsi(int idSala,
                     String nome,
                     String orariApertura,
                     int capienza,
                     boolean disponibile,
                     String orarioCorso) {
        super(idSala, nome, orariApertura, capienza, disponibile);
        this.orarioCorso = orarioCorso;
    }

    public String getOrarioCorso() {
        return orarioCorso;
    }

    public void setOrarioCorso(String orarioCorso) {
        this.orarioCorso = orarioCorso;
    }

    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Tipo sala: Sala Corsi\n");
        sb.append("Orario corso: ").append(orarioCorso).append("\n");
        return sb.toString();
    }
}
