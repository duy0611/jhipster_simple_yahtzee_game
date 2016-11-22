package fi.vaasa.yahtzee.domain.exception;

public class GameNotStartedException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public GameNotStartedException(String message) {
		super(message);
	}
	
	public GameNotStartedException(String message, Throwable e) {
		super(message, e);
	}
}
