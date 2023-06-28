package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.dto.ShortBookingDtoToResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, Item item, User user, Status status) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                status);
    }

    public ShortBookingDtoToResponse toShortBookingDtoToResponse(Booking booking) {
        return new ShortBookingDtoToResponse(booking.getId(), booking.getStart(),
                booking.getEnd(), booking.getBooker().getId());
    }

    public BookingDtoToResponse toBookingDtoToResponse(Booking booking) {
        return new BookingDtoToResponse(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                toBookingItemDto(booking.getItem()),
                toBookingBookerDto(booking.getBooker()),
                booking.getStatus());
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem().getId());
    }

    private BookingDtoToResponse.BookingBookerDto toBookingBookerDto(User user) {
        return new BookingDtoToResponse.BookingBookerDto(user.getId());
    }

    private BookingDtoToResponse.BookingItemDto toBookingItemDto(Item item) {
        return new BookingDtoToResponse.BookingItemDto(item.getId(), item.getName());
    }
}
