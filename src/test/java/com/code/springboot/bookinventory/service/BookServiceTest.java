package com.code.springboot.bookinventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.code.springboot.bookinventory.dao.BookRepository;
import com.code.springboot.bookinventory.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository; // Mock dependencies

    @InjectMocks
    private BookServiceImpl bookService; // Use a concrete implementation

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testFindAllBooks() {
        Book book1 = new Book(12, "The Mythical Man-Month", "Frederick P. Brooks Jr.", "Software Engineering",
                new BigDecimal("40.99"), 5, LocalDate.of(1975, 8, 25));

        Book book2 = new Book(13, "Clean Code", "Robert C. Martin", "Programming",
                new BigDecimal("35.99"), 10, LocalDate.of(2008, 8, 1));

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Book> books = bookService.findAll();
        assertEquals(2, books.size());
    }
}
