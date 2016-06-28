package be.home.common.exceptions;

public class ApplicationException extends RuntimeException {

	  /**
	 * 
	 */
	private static final long serialVersionUID = -5910289446480826550L;
	/**
	 * 
	 */
//	
	protected String errorMessage;

	  public ApplicationException(String argErrorMessage,
	                              Exception cause) {
	    super(argErrorMessage, cause);
	    errorMessage = argErrorMessage;
	  }

	  public ApplicationException(String argErrorMessage) {
	    this(argErrorMessage, null);
	  }

}
