package pl.banzaijiujitsu.backend.exception;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String message) {
        super(message);
    }

    public InvalidStatusException() {
    }
}
