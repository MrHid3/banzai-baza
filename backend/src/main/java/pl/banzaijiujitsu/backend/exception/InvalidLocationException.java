package pl.banzaijiujitsu.backend.exception;

public class InvalidLocationException extends RuntimeException {
    public InvalidLocationException(){}

    public InvalidLocationException(String message) {
        super(message);
    }
}
