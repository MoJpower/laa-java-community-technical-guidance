package uk.gov.justice.laa.library.tutorial.exception;

/** Raised when a supplied ISBN already identifies another catalogue book. */
public class DuplicateIsbnException extends RuntimeException {
  public DuplicateIsbnException(String isbn, Throwable cause) {
    super("ISBN %s is already in the catalogue".formatted(isbn), cause);
  }
}
