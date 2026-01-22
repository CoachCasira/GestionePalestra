package model;

public class AbbonamentoBasico extends Abbonamento {

    public AbbonamentoBasico() {
        super("BASE", 30, "Accesso fascia oraria standard");
    }

    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Con questo abbonamento il cliente può accedere ")
          .append("ESCLUSIVAMENTE alla SALA PESI, ")
          .append("dove può allenarsi con i macchinari e i pesi liberi.\n")
          .append("Non sono inclusi corsi di gruppo né accesso alla SPA.");
        return sb.toString();
    }
}
