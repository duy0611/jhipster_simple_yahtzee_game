package fi.vaasa.yahtzee.domain.exception;

public class DuplicateCategoryException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DuplicateCategoryException(String message) {
		super(message);
	}
	
	public DuplicateCategoryException(String message, Throwable e) {
		super(message, e);
	}
}
