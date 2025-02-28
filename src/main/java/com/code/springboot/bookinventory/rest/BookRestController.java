package com.code.springboot.bookinventory.rest;

import com.code.springboot.bookinventory.entity.Book;
import com.code.springboot.bookinventory.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class BookRestController {

    private BookService bookService;

    // inject employee dao layer
    @Autowired
    public BookRestController(BookService theBookService){
        bookService = theBookService;
    }

    // adding endpoint "/books" to get list of all books
    @GetMapping("/books")
    public List<Book> findAll(){
        return bookService.findAll();
    }

    // adding endpoint to get book by isbnId
    @GetMapping("/books/{isbnId}")
    public Book getBook(@PathVariable String isbnId){
        Book theBook = bookService.findByIsbn(isbnId);
        if (theBook == null){
            throw new NoSuchElementException("Book id not found: " + isbnId);
        }
        return theBook;
    }

    // adding endpoint to get book by title
    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }

}
