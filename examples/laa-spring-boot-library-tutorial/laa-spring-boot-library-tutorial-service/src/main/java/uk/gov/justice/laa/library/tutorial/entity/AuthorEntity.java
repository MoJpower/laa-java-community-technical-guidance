package uk.gov.justice.laa.library.tutorial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Persistent author referenced by catalogue books. */
@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
public class AuthorEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200, unique = true)
  private String name;

  public AuthorEntity(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof AuthorEntity author
            && id != null
            && Objects.equals(id, author.id));
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
