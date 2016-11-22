package fi.vaasa.yahtzee.domain.exception;

public class InvalidGameException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidGameException(String message) {
		super(message);
	}
	
	public InvalidGameException(String message, Throwable e) {
		super(message, e);
	}
}
