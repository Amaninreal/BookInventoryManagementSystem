package com.code.springboot.bookinventory.dao;

import com.code.springboot.bookinventory.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookInterface extends JpaRepository<Book, Integer> {
}
