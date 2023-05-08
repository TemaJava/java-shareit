package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDtoToResponse createBooking(long id, BookingDto bookingDto);

    List<BookingDtoToResponse> getBookingByBooker(long userId, String state);

    List<BookingDtoToResponse> getBookingByOwner(long userId, String state);

    BookingDtoToResponse changeBookingStatus(long userId, long bookingId, boolean approved);

    BookingDtoToResponse getBookingInfo(long userId, long bookingId);
}
