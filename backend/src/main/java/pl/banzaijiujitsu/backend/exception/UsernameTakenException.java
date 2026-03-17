package pl.banzaijiujitsu.backend.exception;

public class UsernameTakenException extends Exception {
    public UsernameTakenException() {}

    public UsernameTakenException(String message) {
        super(message);
    }
}
