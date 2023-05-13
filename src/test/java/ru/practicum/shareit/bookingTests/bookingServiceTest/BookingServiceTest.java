package ru.practicum.shareit.bookingTests.bookingServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IncorrectStateException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private LocalDateTime now = LocalDateTime.now();

    private LocalDateTime bookingStart = now.minusHours(2);

    private LocalDateTime bookingEnd = now.plusDays(3);

    private User user;

    private User anotherUser;

    private Item item;

    private Booking booking;


    @BeforeEach
    void creteModel() {
         user = new User(1L, "User name", "user@mail.com");
         anotherUser = new User(2L, "Another name", "another@mail.com");
         item = new Item(1L, "itemName", "itemDesc", true, user, null);
         booking = new Booking(1L, bookingStart, bookingEnd, item, anotherUser, Status.WAITING);
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoToResponse bookingDtoResponse = bookingService.createBooking(anotherUser.getId(),
                BookingMapper.toBookingDto(booking));
        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(bookingStart, bookingDtoResponse.getStart());
        assertEquals(bookingEnd, bookingDtoResponse.getEnd());
        assertEquals(anotherUser.getId(), bookingDtoResponse.getBooker().getId());
    }

    @Test
    void createBookingWithNotAvailableItemShouldThrowExceptionTest() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(user.getId(),
                        BookingMapper.toBookingDto(booking)));
        assertEquals("Вы не можете забронировать этот предмет", exception.getMessage());
    }

    @Test
    void createBookingWithNotCreatedItemShouldThrowExceptionTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(user.getId(),
                        BookingMapper.toBookingDto(booking)));
        assertEquals("Предмет с id " + item.getId() + " не обнаружен", exception.getMessage());
    }

    @Test
    void getBookingByOwnerWithStatusAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemUserIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByOwner(user.getId(),
                "ALL", any());
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByOwnerWithStatusCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerCurrent(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByOwner(user.getId(), "CURRENT",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByOwnerWithStatusPast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerPast(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByOwner(user.getId(), "PAST",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByOwnerWithStatusFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerFuture(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByOwner(user.getId(), "FUTURE",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByOwnerWithStatusWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByOwner(user.getId(), "WAITING",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByOwnerWithStatusRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByOwner(user.getId(), "REJECTED",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByBookerWithStatusAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByBooker(user.getId(),
                "ALL", any());
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByBookerWithStatusCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerCurrent(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByBooker(user.getId(), "CURRENT",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByBookerWithStatusPast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerPast(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByBooker(user.getId(), "PAST",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByBookerWithStatusFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerFuture(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByBooker(user.getId(), "FUTURE",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByBookerWithStatusWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByBooker(user.getId(), "WAITING",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByBookerWithStatusRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingDtoToResponse> bookingDtoResponses = bookingService.getBookingByBooker(user.getId(),
                "REJECTED",
                Pagination.toPageable(1, 1));
        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(item.getId(), bookingDtoResponses.get(0).getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponses.get(0).getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingByBookerWithUnknownStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        IncorrectStateException exception = assertThrows(IncorrectStateException.class,
                () -> bookingService.getBookingByBooker(user.getId(), "NEWSTATE",
                        Pagination.toPageable(1, 1)));
        assertEquals("Unknown state: NEWSTATE", exception.getMessage());
    }

    @Test
    void getBookingByOwnerWithUnknownStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        IncorrectStateException exception = assertThrows(IncorrectStateException.class,
                () -> bookingService.getBookingByOwner(user.getId(), "NEWSTATE",
                        Pagination.toPageable(1, 1)));

        assertEquals("Unknown state: NEWSTATE", exception.getMessage());
    }

    @Test
    void changeBookingStatusTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoToResponse bookingDtoResponse = bookingService.changeBookingStatus(user.getId(), booking.getId(),
                true);
        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(item.getId(), bookingDtoResponse.getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponse.getBooker().getId());
        assertEquals(Status.APPROVED, bookingDtoResponse.getStatus());
    }

    @Test
    void changeBookingStatusWithWrongUserTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(anotherUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.changeBookingStatus(
                        anotherUser.getId(),
                        booking.getId(),
                        true));
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        BookingDtoToResponse bookingDtoResponse = bookingService.getBookingInfo(booking.getId(), user.getId());
        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(item.getId(), bookingDtoResponse.getItem().getId());
        assertEquals(anotherUser.getId(), bookingDtoResponse.getBooker().getId());
        assertEquals(Status.WAITING, bookingDtoResponse.getStatus());
    }
}


