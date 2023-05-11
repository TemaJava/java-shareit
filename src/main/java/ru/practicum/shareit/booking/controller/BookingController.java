package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.pagination.Pagination;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoToResponse create(@RequestHeader("X-Sharer-User-Id") long id, @Valid
                                       @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoToResponse changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoToResponse getInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoToResponse> getByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        return bookingService.getBookingByBooker(userId, state, Pagination.toPageable(from, size));
    }

    @GetMapping("/owner")
    public List<BookingDtoToResponse> getByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        return bookingService.getBookingByOwner(userId, state, Pagination.toPageable(from, size));
    }
}
