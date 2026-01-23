package action;

import java.awt.Window;

public interface RegistrazioneViewContract {
    void setActions(RegistrazioneActions actions);

    void mostraMessaggioInfo(String msg);
    void mostraMessaggioErrore(String msg);

    void close();          // chiude la finestra corrente
    Window asWindow();    // navigazione verso login (senza far dipendere il controller dalle view)
}