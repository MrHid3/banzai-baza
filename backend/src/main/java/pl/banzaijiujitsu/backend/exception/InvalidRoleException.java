package pl.banzaijiujitsu.backend.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message) {
        super(message);
    }

    public InvalidRoleException() {
    }

    ;
}
