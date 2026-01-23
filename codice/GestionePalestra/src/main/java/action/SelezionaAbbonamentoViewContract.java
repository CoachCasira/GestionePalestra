package action;

public interface SelezionaAbbonamentoViewContract {
    void setActions(SelezionaAbbonamentoActions actions);

    void mostraMessaggioInfo(String msg);
    void mostraMessaggioErrore(String msg);

    void close();
    void showView();
}
