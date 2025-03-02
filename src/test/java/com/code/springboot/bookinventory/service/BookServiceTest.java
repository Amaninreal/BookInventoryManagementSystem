package com.code.springboot.bookinventory.service;

import com.code.springboot.bookinventory.dao.BookRepository;
import com.code.springboot.bookinventory.entity.Book;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private List<Book> mockBooks;

    @BeforeEach
    void setUp() {
        mockBooks = List.of(
                new Book(12, "The Mythical Man-Month", "Frederick P. Brooks Jr.", "Software Engineering",
                        new BigDecimal("40.99"), 5, LocalDate.of(1975, 8, 25)),
                new Book(13, "Clean Code", "Robert C. Martin", "Programming",
                        new BigDecimal("35.99"), 10, LocalDate.of(2008, 8, 1))
        );

        given(bookRepository.findAll()).willReturn(mockBooks);
    }

    @Test
    public void testFindAllBooks() {
        List<Book> books = bookService.findAll();
        System.out.println("Returned books: " + books);

        assertThat(books).hasSize(2);
    }
}
