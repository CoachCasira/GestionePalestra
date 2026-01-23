package action;

import model.Cliente;

public interface PrenotaCorsoViewContract {

    // wiring (la view espone solo l’interfaccia delle azioni)
    void setAction(PrenotaCorsoAction action);

    // API usate dal controller
    Cliente getCliente();

    void setCorsi(String[] nomi);
    void setDescrizioneCorso(String testo);
    void setLezioni(String[] righe);

    int getIndiceCorsoSelezionato();
    int getIndiceLezioneSelezionata();

    // messaggi (così il controller non usa direttamente ThemedDialog)
    void mostraInfo(String titolo, String messaggio);
    void mostraErrore(String titolo, String messaggio);

    // navigazione base
    void close();
}
