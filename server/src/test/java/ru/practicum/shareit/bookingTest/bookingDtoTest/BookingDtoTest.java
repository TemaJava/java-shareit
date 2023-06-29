package ru.practicum.shareit.bookingTest.bookingDtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    private BookingDto bookingDto;

    @BeforeEach
    void createModel() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusDays(1);
        LocalDateTime end = now.plusDays(2);

        User user = new User(1L, "User name", "user@mail.com");
        User anotherUser = new User(2L, "another name", "another@mail.com");

        Item item = new Item(1L, "name", "desc", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(1),
                item, anotherUser, null);
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void dtoTest() throws Exception {
        JsonContent<BookingDto> result = json.write(bookingDto);
        Integer id = Math.toIntExact(bookingDto.getId());
        Integer itemId = Math.toIntExact(bookingDto.getItemId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(id);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(itemId);
    }
}
