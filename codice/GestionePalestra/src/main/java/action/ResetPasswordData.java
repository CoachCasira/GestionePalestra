package action;

public class ResetPasswordData {
    public final String codice;
    public final String nuovaPassword;
    public final String confermaPassword;

    public ResetPasswordData(String codice, String nuovaPassword, String confermaPassword) {
        this.codice = codice;
        this.nuovaPassword = nuovaPassword;
        this.confermaPassword = confermaPassword;
    }
}
