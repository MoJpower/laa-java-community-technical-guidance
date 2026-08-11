package uk.gov.justice.laa.library.tutorial.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import uk.gov.justice.laa.library.tutorial.TestcontainersConfig;
import uk.gov.justice.laa.library.tutorial.model.Book;
import uk.gov.justice.laa.library.tutorial.model.BookList;
import uk.gov.justice.laa.library.tutorial.model.CreateBookRequest;
import uk.gov.justice.laa.library.tutorial.model.ErrorResponse;
import uk.gov.justice.laa.library.tutorial.repository.AuthorRepository;
import uk.gov.justice.laa.library.tutorial.repository.BookRepository;

@Import(TestcontainersConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class BookApiIntegrationTest {
  @Autowired private TestRestTemplate restTemplate;
  @Autowired private BookRepository bookRepository;
  @Autowired private AuthorRepository authorRepository;

  @BeforeEach
  void cleanDatabase() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
  }

  @Test
  void createsRetrievesAndListsABookThroughThePublicApi() {
    var request =
        new CreateBookRequest("Kindred", "Octavia E. Butler")
            .publicationYear(1979)
            .isbn("9780807083697");

    var createdResponse = restTemplate.postForEntity("/api/v1/books", request, Book.class);
    assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Book created = createdResponse.getBody();
    assertThat(created).isNotNull();

    var retrieved = restTemplate.getForEntity("/api/v1/books/" + created.getId(), Book.class);
    assertThat(retrieved.getBody()).usingRecursiveComparison().isEqualTo(created);

    var listed =
        restTemplate.exchange(
            URI.create(
                restTemplate.getRootUri()
                    + "/api/v1/books?limit=1&page=0&authorName=octavia%20e.%20butler&publicationYear=1979"),
            HttpMethod.GET,
            HttpEntity.EMPTY,
            BookList.class);
    assertThat(listed.getBody()).isNotNull();
    assertThat(listed.getBody().getBooks()).containsExactly(created);
    assertThat(listed.getBody().getTotalItems()).isEqualTo(1L);
    assertThat(listed.getBody().getTotalPages()).isEqualTo(1);
  }

  @Test
  void reportsDuplicateSuppliedIsbnWithStableError() {
    var first =
        new CreateBookRequest("Kindred", "Octavia E. Butler").isbn("9780807083697");
    var second =
        new CreateBookRequest("Another Kindred", "Octavia E. Butler")
            .isbn("9780807083697");
    restTemplate.postForEntity("/api/v1/books", first, Book.class);

    var response = restTemplate.postForEntity("/api/v1/books", second, ErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getCode()).isEqualTo("DUPLICATE_ISBN");
  }
}
