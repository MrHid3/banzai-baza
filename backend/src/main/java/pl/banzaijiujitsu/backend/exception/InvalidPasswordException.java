package pl.banzaijiujitsu.backend.exception;

public class InvalidPasswordException extends Exception {

    public InvalidPasswordException() {}

    public InvalidPasswordException(String message) {
        super(message);
    }
}
