package uk.gov.justice.laa.library.tutorial.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.library.tutorial.exception.BookNotFoundException;
import uk.gov.justice.laa.library.tutorial.exception.GlobalExceptionHandler;
import uk.gov.justice.laa.library.tutorial.model.Book;
import uk.gov.justice.laa.library.tutorial.model.BookList;
import uk.gov.justice.laa.library.tutorial.service.BookService;

@WebMvcTest({BookController.class, GlobalExceptionHandler.class})
class BookControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockitoBean private BookService bookService;

  @Test
  void createsBookAtPublicContract() throws Exception {
    when(bookService.create(any()))
        .thenReturn(new Book(1L, "Kindred", "Octavia E. Butler"));

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"title\":\"Kindred\",\"authorName\":\"Octavia E. Butler\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Kindred"));
  }

  @Test
  void rejectsInvalidTitle() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"title\":\"   \",\"authorName\":\"Octavia E. Butler\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
  }

  @Test
  void translatesMissingBook() throws Exception {
    when(bookService.get(99L)).thenThrow(new BookNotFoundException(99L));

    mockMvc
        .perform(get("/api/v1/books/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));
  }

  @Test
  void bindsListLimitAndReturnsBoundedShape() throws Exception {
    when(bookService.list(1, 0, null, null))
        .thenReturn(new BookList(java.util.List.of(), 1, 0, 0L, 0));

    mockMvc
        .perform(get("/api/v1/books?limit=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.limit").value(1))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.totalItems").value(0))
        .andExpect(jsonPath("$.books").isArray());
  }

  @Test
  void rejectsListLimitAboveContractMaximum() throws Exception {
    mockMvc
        .perform(get("/api/v1/books?limit=101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
  }
}
