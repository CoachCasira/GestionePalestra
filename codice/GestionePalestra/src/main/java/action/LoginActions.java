package action;

public interface LoginActions {
    void handleLogin(String username, String password);
    void handlePasswordDimenticata();
    void handleRegistrazione();
}
