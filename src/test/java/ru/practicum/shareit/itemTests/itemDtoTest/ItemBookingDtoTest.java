package ru.practicum.shareit.itemTests.itemDtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ShortBookingDtoToResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDtoToResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemBookingDtoTest {

    @Autowired
    private JacksonTester<ItemBookingDto> json;

    private ItemBookingDto item1DtoBooking;

    @BeforeEach
    void createModel() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User user = new User(1L, "User name", "user@mail.com");
        User anotherUser = new User(2L, "another name", "another@mail.com");
        Item item = new Item(1L, "name", "desc", true, user, null);
        Booking bookingNext = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusHours(1),
                item, anotherUser, Status.WAITING);
        Booking bookingLast = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1),
                item, anotherUser, Status.WAITING);
        ShortBookingDtoToResponse shortBookingDtoToResponseLast = BookingMapper
                .toShortBookingDtoToResponse(bookingLast);
        ShortBookingDtoToResponse nextBookingDtoToResponseLast = BookingMapper
                .toShortBookingDtoToResponse(bookingNext);
        Request request = new Request(1L, anotherUser, "desc", now);
        Comment comment = new Comment(1L, "text", item, anotherUser, now);
        CommentDtoToResponse commentDto = CommentMapper.toCommentDtoToResponse(comment);

        item1DtoBooking = new ItemBookingDto(1L, "name", "desc", true,
                shortBookingDtoToResponseLast,
                nextBookingDtoToResponseLast, List.of(commentDto));
    }

    @Test
    void itemBookingDtoTest() throws Exception {
        JsonContent<ItemBookingDto> result = json.write(item1DtoBooking);

        Integer value = Math.toIntExact(item1DtoBooking.getId());
        Integer lasBookingId = Math.toIntExact(item1DtoBooking.getLastBooking().getId());
        Integer nextBookingId = Math.toIntExact(item1DtoBooking.getNextBooking().getId());

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(value);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(item1DtoBooking.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(item1DtoBooking.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(item1DtoBooking.getAvailable());
        assertThat(result).extractingJsonPathNumberValue(
                "$.lastBooking.id").isEqualTo(lasBookingId);
        assertThat(result).extractingJsonPathNumberValue(
                "$.nextBooking.id").isEqualTo(nextBookingId);
    }
}
