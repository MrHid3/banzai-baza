package pl.banzaijiujitsu.backend.exception;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String message) {
        super(message);
    }

    public UserNotActiveException() {
    }

    ;
}
