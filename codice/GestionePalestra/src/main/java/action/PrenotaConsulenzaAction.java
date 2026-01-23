package action;

public interface PrenotaConsulenzaAction {
    void handleTipoSelezionato(String tipo);
    void handleDipendenteSelezionato(String nomeCompleto);
    void handleConfermaPrenotazione();
    void handleAnnulla();
}
