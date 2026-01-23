package action;

public interface RegistrazioneActions {
    void handleConferma(String username,
                        String password,
                        String nome,
                        String cognome,
                        String cf,
                        String luogoNascita,
                        String dataNascita,
                        String iban,
                        String email);

    void handleAnnulla();
}
