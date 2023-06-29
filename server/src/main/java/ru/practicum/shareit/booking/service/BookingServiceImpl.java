package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.IncorrectStateException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoToResponse createBooking(long id, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new ObjectNotFoundException("Предмет с id " + id + " не обнаружен");
        });
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException("Ошибка пользователя");
        });
        if (item.getUser().getId() == id) throw new ObjectNotFoundException("Вы не можете забронировать этот предмет");
        if (!item.getAvailable()) throw new BookingException("Предмет недоступен для брони");
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user, Status.WAITING));
        return BookingMapper.toBookingDtoToResponse(booking);
    }

    @Override
    public List<BookingDtoToResponse> getBookingByBooker(long userId, String state, Pageable pagination) {
        State statement;
        try {
            statement = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не обнаружен");
        });
        List<Booking> bookings = List.of();
        switch (statement) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerCurrent(userId, LocalDateTime.now(), pagination);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerPast(userId, LocalDateTime.now(), pagination);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerFuture(userId, LocalDateTime.now(), pagination);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(userId, Status.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(userId, Status.REJECTED, pagination);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoToResponse> getBookingByOwner(long userId, String state, Pageable pagination) {
        State statement;
        try {
            statement = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }

        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не обнаружен");
        });
        List<Booking> bookings = List.of();
        switch (statement) {
            case ALL:
                bookings = bookingRepository.findByItemUserIdOrderByStartDesc(userId, pagination);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerCurrent(userId, LocalDateTime.now(), pagination);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerPast(userId, LocalDateTime.now(), pagination);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerFuture(userId, LocalDateTime.now(), pagination);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatus(userId, Status.WAITING, pagination);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatus(userId, Status.REJECTED, pagination);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDtoToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDtoToResponse changeBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Бронь не найдена");
        });
        Item item = booking.getItem();
        if (userId != item.getUser().getId()) {
            throw new ObjectNotFoundException("Изменять статус может только владелец вещи");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new BookingException("Нельзя изменить статус");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoToResponse(booking);
    }

    @Override
    public BookingDtoToResponse getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Бронь не найдена");
        });
        Item item = booking.getItem();
        if (booking.getBooker().getId() != userId && item.getUser().getId() != userId) {
            throw new ObjectNotFoundException("Ошибка доступа - пользователь не создатель и не бронирующий");
        }
        return BookingMapper.toBookingDtoToResponse(booking);
    }
}
