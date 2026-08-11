package uk.gov.justice.laa.library.tutorial.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.justice.laa.library.tutorial.entity.BookEntity;
import uk.gov.justice.laa.library.tutorial.model.Book;
import uk.gov.justice.laa.library.tutorial.model.CreateBookRequest;

/** Converts between generated API models and the internal book representation. */
@Mapper(componentModel = "spring")
public interface BookMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "author", ignore = true)
  BookEntity toEntity(CreateBookRequest request);

  @Mapping(target = "authorName", source = "author.name")
  Book toApi(BookEntity entity);

  List<Book> toApi(List<BookEntity> entities);
}
