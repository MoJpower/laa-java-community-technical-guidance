package uk.gov.justice.laa.library.tutorial.service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.library.tutorial.entity.AuthorEntity;
import uk.gov.justice.laa.library.tutorial.entity.BookEntity;
import uk.gov.justice.laa.library.tutorial.exception.BookNotFoundException;
import uk.gov.justice.laa.library.tutorial.exception.DuplicateIsbnException;
import uk.gov.justice.laa.library.tutorial.mapper.BookMapper;
import uk.gov.justice.laa.library.tutorial.model.Book;
import uk.gov.justice.laa.library.tutorial.model.BookList;
import uk.gov.justice.laa.library.tutorial.model.CreateBookRequest;
import uk.gov.justice.laa.library.tutorial.repository.AuthorRepository;
import uk.gov.justice.laa.library.tutorial.repository.BookRepository;

/** Coordinates transactional book behaviour independently of HTTP concerns. */
@Service
public class BookService {
  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final BookMapper bookMapper;

  /** Creates a service with explicit persistence and mapping dependencies. */
  public BookService(
      BookRepository bookRepository, AuthorRepository authorRepository, BookMapper bookMapper) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
    this.bookMapper = bookMapper;
  }

  /** Creates a book and its author atomically. */
  @Transactional
  public Book create(CreateBookRequest request) {
    AuthorEntity author =
        authorRepository
            .findByNameIgnoreCase(request.getAuthorName())
            .orElseGet(() -> authorRepository.save(new AuthorEntity(request.getAuthorName())));
    BookEntity entity = bookMapper.toEntity(request);
    entity.setAuthor(author);
    try {
      return bookMapper.toApi(bookRepository.save(entity));
    } catch (DataIntegrityViolationException exception) {
      if (request.getIsbn() != null) {
        throw new DuplicateIsbnException(request.getIsbn(), exception);
      }
      throw exception;
    }
  }

  /** Retrieves a book or raises a domain-specific failure. */
  @Transactional(readOnly = true)
  public Book get(Long bookId) {
    return bookMapper.toApi(
        bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId)));
  }

  /** Returns books in identifier order up to the requested bound. */
  @Transactional(readOnly = true)
  public BookList list(int limit, int page, String authorName, Integer publicationYear) {
    var selected =
        bookRepository.findAll(
            catalogueFilters(authorName, publicationYear), PageRequest.of(page, limit));
    return new BookList(
        bookMapper.toApi(selected.getContent()),
        limit,
        page,
        selected.getTotalElements(),
        selected.getTotalPages());
  }

  private Specification<BookEntity> catalogueFilters(
      String authorName, Integer publicationYear) {
    return (root, query, criteriaBuilder) -> {
      var predicates = new ArrayList<Predicate>();
      if (authorName != null) {
        predicates.add(
            criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("author").get("name")),
                authorName.toLowerCase(java.util.Locale.ROOT)));
      }
      if (publicationYear != null) {
        predicates.add(criteriaBuilder.equal(root.get("publicationYear"), publicationYear));
      }
      query.orderBy(criteriaBuilder.asc(root.get("id")));
      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
