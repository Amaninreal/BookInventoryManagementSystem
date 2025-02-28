package com.code.springboot.bookinventory.rest;

import com.code.springboot.bookinventory.entity.Book;
import com.code.springboot.bookinventory.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/inventory")
public class BookRestController {
    private final BookService bookService;
    private static final Logger logger = LoggerFactory.getLogger(BookRestController.class);

    @Autowired
    public BookRestController(BookService theBookService) {
        this.bookService = theBookService;
    }

    /**
     * Retrieves all books in the inventory.
     */
    @GetMapping("/books")
    public List<Book> findAll() {
        logger.info("Fetching all books from the inventory...");
        return bookService.findAll();
    }

    /**
     * Retrieves a book by its ISBN.
     */
    @GetMapping("/books/{isbnId}")
    public ResponseEntity<Book> getBook(@PathVariable int isbnId) {
        logger.info("Fetching book with ISBN: {}", isbnId);
        return bookService.findByIsbn(isbnId)
                .map(book -> {
                    logger.info("Book found: {}", book);
                    return ResponseEntity.ok(book);
                })
                .orElseGet(() -> {
                    logger.warn("Book with ISBN {} not found.", isbnId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Searches for books by title.
     */
    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam String title) {
        logger.info("Searching books with title: {}", title);
        return bookService.searchByTitle(title);
    }

    /**
     * Adds a new book to the inventory.
     */
    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book theBook) {
        theBook.setIsbn(0);
        Book savedBook = bookService.save(theBook);
        logger.info("New book added: {}", savedBook);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    /**
     * Updates an existing book by its ISBN.
     */
    @PutMapping("/books/{isbn}")
    public ResponseEntity<Book> updateBookById(@PathVariable int isbn, @RequestBody Book updatedBook) {
        logger.info("Updating book with ISBN: {}", isbn);
        return bookService.findByIsbn(isbn)
                .map(existingBook -> {
                    updateBookDetails(existingBook, updatedBook);
                    Book savedBook = bookService.save(existingBook);
                    logger.info("Updated book details: {}", savedBook);
                    return ResponseEntity.ok(savedBook);
                })
                .orElseGet(() -> {
                    logger.warn("Failed to update - Book with ISBN {} not found.", isbn);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Partially updates a book by its ISBN.
     */
    @PatchMapping("/books/{isbn}")
    public ResponseEntity<Book> updateBookPartially(@PathVariable int isbn, @RequestBody Map<String, Object> updates) {
        logger.info("Partially updating book with ISBN: {}", isbn);
        return bookService.findByIsbn(isbn)
                .map(existingBook -> {
                    updates.forEach((key, value) -> applyUpdate(existingBook, key, value));
                    Book updatedBook = bookService.save(existingBook);
                    logger.info("Book updated successfully: {}", updatedBook);
                    return ResponseEntity.ok(updatedBook);
                })
                .orElseGet(() -> {
                    logger.warn("Partial update failed - Book with ISBN {} not found.", isbn);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Deletes a book by its ISBN.
     */
    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable int isbn) {
        logger.info("Deleting book with ISBN: {}", isbn);
        return bookService.findByIsbn(isbn)
                .map(book -> {
                    bookService.deleteByIsbn(isbn);
                    logger.info("Deleted book with ISBN: {}", isbn);
                    return ResponseEntity.ok("Deleted book with ISBN " + isbn);
                })
                .orElseGet(() -> {
                    logger.warn("Delete operation failed - Book with ISBN {} not found.", isbn);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Book with ISBN " + isbn + " not found.");
                });
    }

    /**
     * Updates book details.
     */
    private void updateBookDetails(Book existingBook, Book updatedBook) {
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setQuantityInStock(updatedBook.getQuantityInStock());
        existingBook.setPublicationDate(updatedBook.getPublicationDate());
    }

    /**
     * Dynamically applies updates to the book object.
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
        logger.info("Updated field '{}' with value '{}'", key, value);
    }

    // Check stock level for a book
    @GetMapping("/books/{isbn}/stock")
    public ResponseEntity<String> checkStock(@PathVariable int isbn) {
        return bookService.findByIsbn(isbn)
                .map(book -> ResponseEntity.ok("Stock for ISBN " + isbn + ": " + book.getQuantityInStock()))
                .orElseGet(() -> {
                    logger.warn("Stock check failed - Book with ISBN {} not found", isbn);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ISBN " + isbn + " not found.");
                });
    }

    // Purchase a book and reduce stock
    @PostMapping("/purchase/{isbn}")
    public ResponseEntity<String> purchaseBook(@PathVariable int isbn, @RequestParam int quantity) {
        return bookService.findByIsbn(isbn)
                .map(book -> {
                    if (book.getQuantityInStock() < quantity) {
                        logger.warn("Purchase failed - Not enough stock for ISBN {}", isbn);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Insufficient stock for book with ISBN " + isbn);
                    }
                    book.setQuantityInStock(book.getQuantityInStock() - quantity);
                    bookService.save(book);
                    logger.info("Purchased {} copies of book with ISBN {}", quantity, isbn);
                    return ResponseEntity.ok("Successfully purchased " + quantity + " copies of ISBN " + isbn);
                })
                .orElseGet(() -> {
                    logger.warn("Purchase failed - Book with ISBN {} not found", isbn);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ISBN " + isbn + " not found.");
                });
    }
}