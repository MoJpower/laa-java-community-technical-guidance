package uk.gov.justice.laa.library.tutorial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Persistent book representation, kept separate from generated API models. */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class BookEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(name = "publication_year")
  private Integer publicationYear;

  @Column(length = 13)
  private String isbn;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private AuthorEntity author;

  @Override
  public boolean equals(Object other) {
    return this == other
        || (other instanceof BookEntity book && id != null && Objects.equals(id, book.id));
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
