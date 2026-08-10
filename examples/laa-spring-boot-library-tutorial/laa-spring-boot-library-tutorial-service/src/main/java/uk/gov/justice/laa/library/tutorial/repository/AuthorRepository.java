package uk.gov.justice.laa.library.tutorial.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.laa.library.tutorial.entity.AuthorEntity;

/** Persistence access for catalogue authors. */
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
  Optional<AuthorEntity> findByNameIgnoreCase(String name);
}
