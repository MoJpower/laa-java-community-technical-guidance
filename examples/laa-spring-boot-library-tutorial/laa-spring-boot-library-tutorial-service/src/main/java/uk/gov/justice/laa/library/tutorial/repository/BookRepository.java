package uk.gov.justice.laa.library.tutorial.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uk.gov.justice.laa.library.tutorial.entity.BookEntity;

/** Persistence access for catalogue books. */
public interface BookRepository
    extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {
  List<BookEntity> findAllByOrderById(Pageable pageable);

  List<BookEntity> findByAuthorNameIgnoreCaseOrderById(String authorName, Pageable pageable);

  List<BookEntity> findByPublicationYearOrderById(Integer publicationYear, Pageable pageable);
}
