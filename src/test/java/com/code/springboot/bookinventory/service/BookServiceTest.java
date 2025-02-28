package com.code.springboot.bookinventory.service;

import com.code.springboot.bookinventory.entity.Book;
import com.code.springboot.bookinventory.dao.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository; // Mocked dependency

    @InjectMocks
    private BookServiceImpl bookService; // Injecting the mock

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByIsbn_BookExists() {
        Book book = new Book(101, "Spring Boot", "John Doe");
        when(bookRepository.findById(101)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findByIsbn(101);

        assertTrue(result.isPresent());
        assertEquals("Spring Boot", result.get().getTitle());
    }

    @Test
    void testFindByIsbn_BookNotFound() {
        when(bookRepository.findById(102)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.findByIsbn(102);

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveBook() {
        Book book = new Book(0, "Java Basics", "Jane Doe");
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.save(book);

        assertNotNull(savedBook);
        assertEquals("Java Basics", savedBook.getTitle());
    }

    @Test
    void testDeleteByIsbn() {
        doNothing().when(bookRepository).deleteById(11);

        bookService.deleteByIsbn(11);

        verify(bookRepository, times(1)).deleteById(11);
    }
}
