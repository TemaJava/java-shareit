package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
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
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingException("неверное время");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingException("Start = end");
        }
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setBookerId(id);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
        return BookingMapper.toBookingDtoToResponse(booking);
    }

    @Override
    public List<BookingDtoToResponse> getBookingByBooker(long userId, String state) {
        State statement;
        try {
            statement = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не обнаружен");
        });
        List<Booking> list = new ArrayList<>();
        switch (statement) {
            case ALL:
                list.addAll(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
                break;
            case CURRENT:
                list.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now()));
                break;
            case PAST:
                list.addAll(bookingRepository.findByBookerPast(userId, LocalDateTime.now()));
                break;
            case FUTURE:
                list.addAll(bookingRepository.findByBookerFuture(userId, LocalDateTime.now()));
                break;
            case WAITING:
                list.addAll(bookingRepository.findByBookerAndStatus(userId, Status.WAITING));
                break;
            case REJECTED:
                list.addAll(bookingRepository.findByBookerAndStatus(userId, Status.REJECTED));
                break;
        }
        return list.stream()
                .map(BookingMapper::toBookingDtoToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoToResponse> getBookingByOwner(long userId, String state) {
        State statement;
        try {
            statement = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }

        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь не обнаружен");
        });
        List<Booking> list = new ArrayList<>();
        switch (statement) {
            case ALL:
                list.addAll(bookingRepository.findByItemUserIdOrderByStartDesc(userId));
                break;
            case CURRENT:
                list.addAll(bookingRepository.findByItemOwnerCurrent(userId, LocalDateTime.now()));
                break;
            case PAST:
                list.addAll(bookingRepository.findByItemOwnerPast(userId, LocalDateTime.now()));
                break;
            case FUTURE:
                list.addAll(bookingRepository.findByItemOwnerFuture(userId, LocalDateTime.now()));
                break;
            case WAITING:
                list.addAll(bookingRepository.findByItemOwnerAndStatus(userId, Status.WAITING));
                break;
            case REJECTED:
                list.addAll(bookingRepository.findByItemOwnerAndStatus(userId, Status.REJECTED));
                break;
        }
        return list.stream().map(BookingMapper::toBookingDtoToResponse)
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
        return BookingMapper.toBookingDtoToResponse(bookingRepository.save(booking));
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
