package com.code.springboot.bookinventory.service;

import com.code.springboot.bookinventory.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> findAll();

    Book findByIsbn(String isbn);

    List<Book> searchByTitle(String title);

    Book save(Book theBook);

    void deleteByIsbn(String isbn);
}
