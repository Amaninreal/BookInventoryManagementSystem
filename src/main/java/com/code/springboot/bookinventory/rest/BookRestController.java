package com.code.springboot.bookinventory.rest;

import com.code.springboot.bookinventory.entity.Book;
import com.code.springboot.bookinventory.exception.ResourceNotFoundException;
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
     * @return List of books
     */
    @GetMapping("/books")
    public List<Book> findAll() {
        logger.info("Fetching all books from the inventory...");
        return bookService.findAll();
    }

    /**
     * Retrieves a book by its ISBN.
     * @param isbnId ISBN of the book
     * @return ResponseEntity containing the book or 404 if not found
     */
    @GetMapping("/books/{isbnId}")
    public ResponseEntity<Book> getBook(@PathVariable int isbnId) {
        logger.info("Fetching book with ISBN: {}", isbnId);
        Book book = bookService.findByIsbn(isbnId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbnId));
        return ResponseEntity.ok(book);
    }

    /**
     * Searches for books by title.
     * @param title Book title to search
     * @return List of books matching the title
     */
    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam String title) {
        logger.info("Searching books with title: {}", title);
        return bookService.searchByTitle(title);
    }

    /**
     * Adds a new book to the inventory.
     * @param theBook Book object to add
     * @return ResponseEntity containing the added book
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
     * @param isbn ISBN of the book to update
     * @param updatedBook Updated book details
     * @return ResponseEntity containing the updated book
     */
    @PutMapping("/books/{isbn}")
    public ResponseEntity<Book> updateBookById(@PathVariable int isbn, @RequestBody Book updatedBook) {
        logger.info("Updating book with ISBN: {}", isbn);
        Book existingBook = bookService.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));

        updateBookDetails(existingBook, updatedBook);
        Book savedBook = bookService.save(existingBook);
        logger.info("Updated book details: {}", savedBook);
        return ResponseEntity.ok(savedBook);
    }

    /**
     * partially updates a book by its ISBN.
     * @param isbn ISBN of the book to update
     * @param updates Map of fields to update
     * @return ResponseEntity containing the updated book
     */
    @PatchMapping("/books/{isbn}")
    public ResponseEntity<Book> updateBookPartially(@PathVariable int isbn, @RequestBody Map<String, Object> updates) {
        logger.info("Partially updating book with ISBN: {}", isbn);
        Book existingBook = bookService.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));

        updates.forEach((key, value) -> applyUpdate(existingBook, key, value));
        Book updatedBook = bookService.save(existingBook);
        logger.info("Book updated successfully: {}", updatedBook);
        return ResponseEntity.ok(updatedBook);
    }


    /**
     * Deletes a book by its ISBN.
     * @param isbn ISBN of the book to delete
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<String> deleteBook(@PathVariable int isbn) {
        logger.info("Deleting book with ISBN: {}", isbn);
        bookService.deleteByIsbn(isbn);
        logger.info("Deleted book with ISBN: {}", isbn);
        return ResponseEntity.ok("Deleted book with ISBN " + isbn);
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

    /**
     * checking stock level for a book.
     * @param isbn ISBN of the book
     * @return ResponseEntity containing stock level or 404 if not found
     */
    @GetMapping("/books/{isbn}/stock")
    public ResponseEntity<String> checkStock(@PathVariable int isbn) {
        Book book = bookService.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));

        return ResponseEntity.ok("Stock for ISBN " + isbn + ": " + book.getQuantityInStock());
    }


    /**
     * handling book purchase and reduces stock quantity.
     * @param isbn ISBN of the book
     * @param quantity Quantity to purchase
     * @return ResponseEntity with purchase status
     */
    @PostMapping("/purchase/{isbn}")
    public ResponseEntity<String> purchaseBook(@PathVariable int isbn, @RequestParam int quantity) {
        Book book = bookService.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));

        if (book.getQuantityInStock() < quantity) {
            logger.warn("Purchase failed - Not enough stock for ISBN {}", isbn);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Insufficient stock for book with ISBN " + isbn);
        }

        book.setQuantityInStock(book.getQuantityInStock() - quantity);
        bookService.save(book);
        logger.info("Purchased {} copies of book with ISBN {}", quantity, isbn);
        return ResponseEntity.ok("Successfully purchased " + quantity + " copies of ISBN " + isbn);
    }
}