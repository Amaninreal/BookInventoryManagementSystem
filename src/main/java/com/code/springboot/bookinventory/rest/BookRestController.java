package com.code.springboot.bookinventory.rest;

import com.code.springboot.bookinventory.entity.Book;
import com.code.springboot.bookinventory.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BookRestController {
    private final BookService bookService;

    // adding constructor-based dependency injection of BookService.
    @Autowired
    public BookRestController(BookService theBookService) {
        this.bookService = theBookService;
    }

    /**
     * Retrieves a list of all available books.
     *
     * @return List of books from the database.
     */
    @GetMapping("/books")
    public List<Book> findAll() {
        return bookService.findAll();
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbnId The ISBN of the book to find.
     * @return ResponseEntity containing the book if found, otherwise 404 Not Found.
     */
    @GetMapping("/books/{isbnId}")
    public ResponseEntity<Book> getBook(@PathVariable int isbnId) {
        return bookService.findByIsbn(isbnId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Searches for books by title.
     *
     * @param title The title or partial title of the book to search.
     * @return A list of books that match the given title.
     */
    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }
    /**
     * handling HTTP POST requests to add a new book to the inventory.
     *
     * @param theBook The book object received in the request body.
     * @return The saved book with its assigned ISBN.
     */
    @PostMapping("/books")
    public Book addBooks(@RequestBody Book theBook){

        // Ensure ISBN is null to enforce a new book entry instead of updating an existing one
        theBook.setIsbn(0);

        return bookService.save(theBook);
    }

    /**
     * updating an existing book by its ISBN.
     *
     * @param isbn The unique identifier of the book to update.
     * @param updatedBook The book object with updated details.
     * @return ResponseEntity with the updated book or 404 if not found.
     */
    @PutMapping("/books/{isbn}")
    public ResponseEntity<Book> updateBookById(@PathVariable int isbn, @RequestBody Book updatedBook) {
        return bookService.findByIsbn(isbn)
                .map(existingBook -> {
                    // Update book details
                    existingBook.setTitle(updatedBook.getTitle());
                    existingBook.setAuthor(updatedBook.getAuthor());
                    existingBook.setGenre(updatedBook.getGenre());
                    existingBook.setPrice(updatedBook.getPrice());
                    existingBook.setQuantityInStock(updatedBook.getQuantityInStock());
                    existingBook.setPublicationDate(updatedBook.getPublicationDate());

                    return ResponseEntity.ok(bookService.save(existingBook));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * partially updates an existing book by its ISBN.
     *
     * @param isbn The unique identifier of the book to update.
     * @param updates A map containing the fields to be updated.
     * @return The updated book after saving it to the database.
     */
    @PatchMapping("/books/{isbn}")
    public ResponseEntity<Book> updateBookPartially(@PathVariable int isbn, @RequestBody Map<String, Object> updates) {
        Optional<Book> existingBook = bookService.findByIsbn(isbn);

        if (existingBook.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        updates.forEach((key, value) -> applyUpdate(existingBook.orElse(null), key, value));

        Book updatedBook = bookService.save(existingBook.orElse(null));
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * applies the update dynamically based on the provided key-value pair.
     *
     * @param book The book entity to be updated.
     * @param key The field to update.
     * @param value The new value to set.
     */
    private void applyUpdate(Book book, String key, Object value) {
        switch (key) {
            case "title" -> book.setTitle((String) value);
            case "author" -> book.setAuthor((String) value);
            case "genre" -> book.setGenre((String) value);
            case "price" -> book.setPrice(new BigDecimal(value.toString()));
            case "quantityInStock" -> book.setQuantityInStock((Integer) value);
            case "publicationDate" -> book.setPublicationDate(LocalDate.parse((String) value));
            default -> throw new IllegalArgumentException("Invalid field: " + key);
        }
    }

    /**
     * Deletes a book by its ISBN.
     *
     * @param isbn The unique identifier of the book to delete.
     * @return A response indicating success or failure.
     */
    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable int isbn) {
        Optional<Book> existingBook = bookService.findByIsbn(isbn);

        // If book is not found, return 404 Not Found
        if (existingBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book with ISBN " + isbn + " not found.");
        }
        bookService.deleteByIsbn(isbn);
        return ResponseEntity.ok("Deleted book with ISBN " + isbn);
    }
}