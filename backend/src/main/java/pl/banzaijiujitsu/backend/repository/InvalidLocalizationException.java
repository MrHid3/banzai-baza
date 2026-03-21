package pl.banzaijiujitsu.backend.repository;

public class InvalidLocalizationException extends RuntimeException {
    public InvalidLocalizationException(){}

    public InvalidLocalizationException(String message) {
        super(message);
    }
}
