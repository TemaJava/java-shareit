package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;

import java.util.List;

public interface BookingService {
    BookingDtoToResponse createBooking(long id, BookingDto bookingDto);

    List<BookingDtoToResponse> getBookingByBooker(long userId, String state, Pageable pagination);

    List<BookingDtoToResponse> getBookingByOwner(long userId, String state, Pageable pagination);

    BookingDtoToResponse changeBookingStatus(long userId, long bookingId, boolean approved);

    BookingDtoToResponse getBookingInfo(long userId, long bookingId);
}
