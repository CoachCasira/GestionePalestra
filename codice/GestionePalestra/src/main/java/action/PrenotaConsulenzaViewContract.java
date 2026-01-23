package action;

import java.awt.Window;

public interface PrenotaConsulenzaViewContract {

    // ---- input dalla view ----
    String getTipoSelezionato();
    String getDataText();
    String getOraText();
    String getDipendenteSelezionato();
    String getNote();

    // ---- output verso la view ----
    void setDescrizioneTipo(String testo);
    void setDipendenti(String[] nomi);
    void setDescrizioneDipendente(String testo);

    // ---- lifecycle ----
    void dispose();

    // ---- owner per i dialog ----
    Window asWindow();
}
