package service;

public class PasswordResetException extends Exception {
    public PasswordResetException(String message) {
        super(message);
    }
    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
