package model;

public class AbbonamentoCorsi extends Abbonamento {

    public AbbonamentoCorsi() {
        super("CORSI", 40, "Corsi di gruppo negli orari previsti");
    }

    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Con questo abbonamento il cliente può accedere ")
          .append("ESCLUSIVAMENTE alla SALA CORSI, ")
          .append("per partecipare ai corsi di gruppo disponibili.\n")
          .append("Non è previsto l’accesso alla sala pesi né alla SPA.");
        return sb.toString();
    }
}
