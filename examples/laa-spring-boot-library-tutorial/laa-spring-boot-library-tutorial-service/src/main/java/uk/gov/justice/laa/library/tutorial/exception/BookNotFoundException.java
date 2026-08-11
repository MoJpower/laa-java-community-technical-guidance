package uk.gov.justice.laa.library.tutorial.exception;

/** Raised when no book exists for a server-assigned identifier. */
public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(Long bookId) {
    super("Book %d was not found".formatted(bookId));
  }
}
