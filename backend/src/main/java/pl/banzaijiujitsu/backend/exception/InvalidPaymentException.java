package pl.banzaijiujitsu.backend.exception;

public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String message) {
        super(message);
    }

    public InvalidPaymentException() {
        super();
    }
}
