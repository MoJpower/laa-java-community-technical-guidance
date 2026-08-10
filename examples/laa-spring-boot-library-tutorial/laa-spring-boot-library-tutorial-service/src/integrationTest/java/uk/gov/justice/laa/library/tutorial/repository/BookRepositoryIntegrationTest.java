package uk.gov.justice.laa.library.tutorial.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.library.tutorial.TestcontainersConfig;
import uk.gov.justice.laa.library.tutorial.entity.AuthorEntity;
import uk.gov.justice.laa.library.tutorial.entity.BookEntity;

@Import(TestcontainersConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class BookRepositoryIntegrationTest {
  @Autowired private BookRepository bookRepository;
  @Autowired private AuthorRepository authorRepository;

  @BeforeEach
  void cleanDatabase() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
  }

  @Test
  void findsAnAuthorsBooksCaseInsensitivelyAndWithinBound() {
    AuthorEntity author = authorRepository.save(new AuthorEntity("Octavia E. Butler"));
    saveBook("Kindred", author);
    saveBook("Parable of the Sower", author);

    var result =
        bookRepository.findByAuthorNameIgnoreCaseOrderById(
            "OCTAVIA E. BUTLER", PageRequest.of(0, 1));

    assertThat(result).extracting(BookEntity::getTitle).containsExactly("Kindred");
  }

  @Test
  void filtersCatalogueByPublicationYear() {
    AuthorEntity author = authorRepository.save(new AuthorEntity("Octavia E. Butler"));
    BookEntity kindred = saveBook("Kindred", author);
    kindred.setPublicationYear(1979);
    bookRepository.save(kindred);
    BookEntity parable = saveBook("Parable of the Sower", author);
    parable.setPublicationYear(1993);
    bookRepository.save(parable);

    var result = bookRepository.findByPublicationYearOrderById(1979, PageRequest.of(0, 20));

    assertThat(result).extracting(BookEntity::getTitle).containsExactly("Kindred");
  }

  private BookEntity saveBook(String title, AuthorEntity author) {
    var book = new BookEntity();
    book.setTitle(title);
    book.setAuthor(author);
    return bookRepository.save(book);
  }
}
