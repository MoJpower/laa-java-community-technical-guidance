CREATE UNIQUE INDEX uk_books_isbn_when_supplied ON books (isbn) WHERE isbn IS NOT NULL;
