package com.code.springboot.bookinventory.service;

import com.code.springboot.bookinventory.dao.BookRepository;
import com.code.springboot.bookinventory.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService{

    private BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository theBookRepository){
        bookRepository = theBookRepository;
    }

    @Override
    public List<Book> findAll(){
        return bookRepository.findAll();
    }

    @Override
    public Book findByIsbn(String isbn) {
        return null;
    }

    @Override
    public Book save(Book theBook) {
        return null;
    }

    @Override
    public void deleteByIsbn(String isbn) {

    }

}
