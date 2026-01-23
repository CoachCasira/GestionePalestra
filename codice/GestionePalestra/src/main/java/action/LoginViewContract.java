package action;

public interface LoginViewContract {
    void setController(LoginActions controller);

    void mostraMessaggioInfo(String msg);
    void mostraMessaggioErrore(String msg);

    String chiediEmailReset();
    ResetPasswordData chiediCodiceENuovaPassword();

    void dispose();
    void setVisible(boolean visible);
}
