package com.code.springboot.bookinventory.dao;

import com.code.springboot.bookinventory.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    // find books by title
    List<Book> findByTitleContainingIgnoreCase(String title);
}
