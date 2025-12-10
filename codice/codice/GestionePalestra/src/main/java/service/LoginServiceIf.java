package service;

import model.Cliente;

public interface LoginServiceIf {

    /**
     * Autentica un utente dato username e password.
     * Restituisce il Cliente con eventuale abbonamento già caricato.
     */
    Cliente autentica(String username, String password) throws LoginException;

    /**
     * Crea un token di reset password per l'email indicata.
     * Per privacy, se l'email non esiste NON lancia eccezioni ma ritorna null.
     * Se esiste, ritorna il codice di reset (utile in ambiente dev).
     */
    String creaTokenReset(String email) throws PasswordResetException;

    /**
     * Verifica il token e aggiorna la password dell'utente.
     */
    void resetPasswordConToken(String codice, String nuovaPassword)
            throws PasswordResetException;
}
