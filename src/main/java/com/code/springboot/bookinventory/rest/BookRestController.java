package com.code.springboot.bookinventory.rest;

import com.code.springboot.bookinventory.entity.Book;
import com.code.springboot.bookinventory.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
