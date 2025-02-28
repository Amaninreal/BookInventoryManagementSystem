package com.code.springboot.bookinventory.service;

import com.code.springboot.bookinventory.dao.BookRepository;
import com.code.springboot.bookinventory.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService{

    private BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository theBookRepository){
        bookRepository = theBookRepository;
    }

    // added implementation for find all books
    @Override
    public List<Book> findAll(){
        return bookRepository.findAll();
    }

    // added implementation for find books by isbn id
    @Override
    public Book findByIsbn(String isbn) {
        Optional<Book> result = bookRepository.findById(isbn);

        // if not found the books return no such elements exceptions
        return result.orElseThrow(() -> new NoSuchElementException("Did not find book with ISBN: " + isbn));
    }

    // adding implementation for find books by title
    @Override
    public List<Book> searchByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);

        // if not found return empty list
        return books.isEmpty() ? Collections.emptyList() : books;
    }

    @Override
    public Book save(Book theBook) {
        return bookRepository.save(theBook);
    }

    @Override
    public void deleteByIsbn(String isbn) {

    }

}
