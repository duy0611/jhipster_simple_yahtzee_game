package fi.vaasa.yahtzee.domain.exception;

public class GameNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public GameNotFoundException(String message) {
		super(message);
	}
	
	public GameNotFoundException(String message, Throwable e) {
		super(message, e);
	}
}
