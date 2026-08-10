package uk.gov.justice.laa.library.tutorial.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.library.tutorial.api.BooksApi;
import uk.gov.justice.laa.library.tutorial.model.Book;
import uk.gov.justice.laa.library.tutorial.model.BookList;
import uk.gov.justice.laa.library.tutorial.model.CreateBookRequest;
import uk.gov.justice.laa.library.tutorial.service.BookService;

/** HTTP adapter for the generated books contract. */
@RestController
public class BookController implements BooksApi {
  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @Override
  public ResponseEntity<Book> createBook(CreateBookRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request));
  }

  @Override
  public ResponseEntity<Book> getBook(Long bookId) {
    return ResponseEntity.ok(bookService.get(bookId));
  }

  @Override
  public ResponseEntity<BookList> listBooks(
      Integer limit, Integer page, String authorName, Integer publicationYear) {
    return ResponseEntity.ok(bookService.list(limit, page, authorName, publicationYear));
  }
}
