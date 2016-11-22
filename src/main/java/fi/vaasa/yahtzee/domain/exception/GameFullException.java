package fi.vaasa.yahtzee.domain.exception;

public class GameFullException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public GameFullException(String message) {
		super(message);
	}
	
	public GameFullException(String message, Throwable e) {
		super(message, e);
	}
}
