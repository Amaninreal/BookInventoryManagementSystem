package com.code.springboot.bookinventory.service;

import com.code.springboot.bookinventory.dao.BookRepository;
import com.code.springboot.bookinventory.entity.Book;
import com.code.springboot.bookinventory.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;

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
    public Optional<Book> findByIsbn(int isbn) {
        return Optional.ofNullable(bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn)));
    }


    // adding implementation for find books by title
    @Override
    public List<Book> searchByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);

        // if not found return empty list
        return books.isEmpty() ? Collections.emptyList() : books;
    }

    // adding implementation for saving books
    @Override
    public Book save(Book theBook) {
        // If the book is new (has no ISBN), generate one
        return bookRepository.save(theBook);
    }

    @Override
    public void deleteByIsbn(int isbn) {
        if (!bookRepository.existsById(isbn)) {
            throw new ResourceNotFoundException("Book not found with ISBN: " + isbn);
        }
        bookRepository.deleteById(isbn);
    }

}
