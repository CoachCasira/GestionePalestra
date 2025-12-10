package service;

public interface RegistrazioneServiceIf {

    /**
     * Esegue l'intera logica di registrazione di un nuovo cliente.
     * Se qualcosa non va (validazione, duplicati, ecc.) lancia una RegistrazioneException
     * con un messaggio da mostrare all'utente.
     */
    void registraNuovoCliente(String username,
                              String password,
                              String nome,
                              String cognome,
                              String cf,
                              String luogoNascita,
                              String dataNascita,
                              String iban,
                              String email) throws RegistrazioneException;
}
