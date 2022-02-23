package be.home.domain.model.service;

public class MP3Exception extends Exception {
    public MP3Exception(String errorMessage) {
        super(errorMessage);
    }
    public MP3Exception(Throwable throwable) {
        super(throwable);
    }
}

