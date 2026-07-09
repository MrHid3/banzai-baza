package pl.banzaijiujitsu.backend.exception;

public class OTPException extends RuntimeException {
    public OTPException(String message) {
        super(message);
    }

    public OTPException() {}
}
