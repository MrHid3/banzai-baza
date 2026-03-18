package pl.banzaijiujitsu.backend.exception;

public class InvalidUuidException extends RuntimeException {
    public InvalidUuidException(String message) {
        super(message);
    }

    public InvalidUuidException() {}
}
