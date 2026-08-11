package uk.gov.justice.laa.library.tutorial.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.library.tutorial.model.ErrorResponse;

/** Translates application and validation failures to the public error contract. */
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BookNotFoundException.class)
  ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException exception) {
    return response(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", exception.getMessage());
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  ResponseEntity<ErrorResponse> handleValidation(Exception exception) {
    return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request validation failed");
  }

  @ExceptionHandler(DuplicateIsbnException.class)
  ResponseEntity<ErrorResponse> handleDuplicateIsbn(DuplicateIsbnException exception) {
    return response(HttpStatus.CONFLICT, "DUPLICATE_ISBN", "ISBN is already in the catalogue");
  }

  private ResponseEntity<ErrorResponse> response(
      HttpStatus status, String code, String message) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(status.value(), code, message));
  }
}
