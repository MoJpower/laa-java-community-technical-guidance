package uk.gov.justice.laa.library.tutorial.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.justice.laa.library.tutorial.entity.AuthorEntity;
import uk.gov.justice.laa.library.tutorial.entity.BookEntity;
import uk.gov.justice.laa.library.tutorial.model.CreateBookRequest;

class BookMapperTest {
  private final BookMapper mapper = Mappers.getMapper(BookMapper.class);

  @Test
  void separatesRequestFromPersistenceRelationship() {
    var request =
        new CreateBookRequest("Kindred", "Octavia E. Butler")
            .publicationYear(1979)
            .isbn("9780807083697");

    BookEntity entity = mapper.toEntity(request);

    assertThat(entity.getId()).isNull();
    assertThat(entity.getAuthor()).isNull();
    assertThat(entity.getTitle()).isEqualTo("Kindred");
  }

  @Test
  void flattensPersistentAuthorIntoApiModel() {
    var author = new AuthorEntity("Octavia E. Butler");
    var entity = new BookEntity();
    entity.setId(7L);
    entity.setTitle("Kindred");
    entity.setAuthor(author);

    assertThat(mapper.toApi(entity).getAuthorName()).isEqualTo("Octavia E. Butler");
  }
}
