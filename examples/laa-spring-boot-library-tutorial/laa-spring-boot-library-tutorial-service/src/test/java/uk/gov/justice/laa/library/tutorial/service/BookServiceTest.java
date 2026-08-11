package uk.gov.justice.laa.library.tutorial.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import uk.gov.justice.laa.library.tutorial.entity.AuthorEntity;
import uk.gov.justice.laa.library.tutorial.entity.BookEntity;
import uk.gov.justice.laa.library.tutorial.exception.BookNotFoundException;
import uk.gov.justice.laa.library.tutorial.mapper.BookMapper;
import uk.gov.justice.laa.library.tutorial.model.CreateBookRequest;
import uk.gov.justice.laa.library.tutorial.repository.AuthorRepository;
import uk.gov.justice.laa.library.tutorial.repository.BookRepository;

class BookServiceTest {
  private final BookRepository bookRepository = mock(BookRepository.class);
  private final AuthorRepository authorRepository = mock(AuthorRepository.class);
  private final BookService service =
      new BookService(
          bookRepository, authorRepository, Mappers.getMapper(BookMapper.class));

  private AuthorEntity author;
  private BookEntity book;

  @BeforeEach
  void setUp() {
    author = new AuthorEntity("Octavia E. Butler");
    book = new BookEntity();
    book.setId(1L);
    book.setTitle("Kindred");
    book.setAuthor(author);
  }

  @Test
  void reusesAuthorWhenCreatingBook() {
    when(authorRepository.findByNameIgnoreCase("Octavia E. Butler"))
        .thenReturn(Optional.of(author));
    when(bookRepository.save(any())).thenReturn(book);

    var created = service.create(new CreateBookRequest("Kindred", "Octavia E. Butler"));

    assertThat(created.getId()).isEqualTo(1L);
    assertThat(created.getAuthorName()).isEqualTo("Octavia E. Butler");
  }

  @Test
  void retrievesAndListsPersistentBooks() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(book)));

    assertThat(service.get(1L).getTitle()).isEqualTo("Kindred");
    assertThat(service.list(1, 0, null, null).getBooks())
        .extracting("title")
        .containsExactly("Kindred");
  }

  @Test
  void reportsMissingBookInDomainLanguage() {
    when(bookRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.get(99L))
        .isInstanceOf(BookNotFoundException.class)
        .hasMessage("Book 99 was not found");
  }
}
