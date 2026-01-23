package action;

public interface HomeAction {
    void onVediAbbonamento();
    void onPrenotaCorso();
    void onPrenotaConsulenza();
    void onVediConsulenza();
    void onVediCorsi();
    void onDisdiciAbbonamento();
    void onLogout();

    // azioni richiamate dai dialog "dettagli" (disdette)
    void onApriDisdettaConsulenza();
    void onApriDisdettaCorso();

    // nuova funzionalità
    void onPanoramicaPalestra();
}
