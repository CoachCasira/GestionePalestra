package model;

public class AbbonamentoCompleto extends Abbonamento {

    public AbbonamentoCompleto() {
        super("COMPLETO", 50, "Accesso completo negli orari standard");
    }

    @Override
    public String getDescrizioneCompleta() {
        StringBuilder sb = new StringBuilder(getDescrizioneBase());
        sb.append("Con questo abbonamento il cliente può accedere alla SALA PESI ")
          .append("per allenarsi liberamente e dispone inoltre di accesso alla SPA\n")
          .append("(ingresso con ticket SPA incluso nell’abbonamento).\n")
          .append("I corsi di gruppo NON sono compresi.");
        return sb.toString();
    }
}
