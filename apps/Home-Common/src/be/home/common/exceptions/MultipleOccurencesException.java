package be.home.common.exceptions;

/**
 * Created by ghyssee on 28/06/2016.
 */
public class MultipleOccurencesException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -5910289446480826551L;
    /**
     *
     */
//
    protected String errorMessage;

    public MultipleOccurencesException(String argErrorMessage,
                                Exception cause) {
        super(argErrorMessage, cause);
        errorMessage = argErrorMessage;
    }

    public MultipleOccurencesException(String message) {
        this("Mulitple occurences found. " + message, null);
    }

}
