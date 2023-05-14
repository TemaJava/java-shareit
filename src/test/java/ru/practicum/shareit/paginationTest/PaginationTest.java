package ru.practicum.shareit.paginationTest;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.pagination.Pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaginationTest {
    @Test
    void createWrongPaginationShouldThrowException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> Pagination.toPageable(-1, -1));
        assertEquals("Ошибка пагинации", exception.getMessage());
    }
}
