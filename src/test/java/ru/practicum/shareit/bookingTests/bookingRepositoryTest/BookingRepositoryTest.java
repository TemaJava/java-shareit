package ru.practicum.shareit.bookingTests.bookingRepositoryTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination.Pagination;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entity;
    @Autowired
    private BookingRepository bookingRepository;

    User user = new User(
            null,
            "name",
            "email@email.ru");
    User anotherUser = new User(
            null,
            "another Name",
            "another@email.ru");
    Item item = new Item(
            null,
            "name",
            "desc",
            true,
            user,
            null);
    Booking booking = new Booking(
            null,
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().minusHours(1),
            item,
            anotherUser,
            null);

    @BeforeEach
    void uploadEntity() {
        entity.persist(user);
        entity.persist(anotherUser);
        entity.persist(item);
        entity.persist(booking);
    }


    @Test
    void findByBookerCurrentTest() {
        List<Booking> bookingList = bookingRepository.findByBookerCurrent(anotherUser.getId(),
                LocalDateTime.now().minusHours(2), Pagination.toPageable(0, 10));
        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerPastTest() {
        List<Booking> bookingList = bookingRepository.findByBookerPast(anotherUser.getId(),
                LocalDateTime.now().plusHours(2),  Pagination.toPageable(0, 10));
        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerFutureTest() {
        List<Booking> bookingList = bookingRepository.findByBookerFuture(anotherUser.getId(),
                LocalDateTime.now().minusHours(4), Pagination.toPageable(0, 10));
        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByBookerAndStatusTest() {
        booking.setStatus(Status.WAITING);
        List<Booking> bookingList = bookingRepository.findByBookerAndStatus(anotherUser.getId(),
                Status.WAITING,  Pagination.toPageable(0, 10));
        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerCurrentTest() {
        List<Booking> bookingList = bookingRepository.findByItemOwnerCurrent(user.getId(),
                LocalDateTime.now().minusHours(2), Pagination.toPageable(0, 10));

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerPastTest() {
        List<Booking> bookingList = bookingRepository.findByItemOwnerPast(user.getId(),
                LocalDateTime.now().plusHours(2), Pagination.toPageable(0, 10));

        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerFutureTest() {
        List<Booking> bookingList = bookingRepository.findByItemOwnerFuture(user.getId(),
                LocalDateTime.now().minusHours(4), Pagination.toPageable(0, 10));
        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void findByItemOwnerAndStatusTest() {
        booking.setStatus(Status.WAITING);
        List<Booking> bookingList = bookingRepository.findByItemOwnerAndStatus(user.getId(),
                Status.WAITING, Pagination.toPageable(0, 10));
        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }

    @Test
    void getAllBookingByItemTest() {
        booking.setStatus(Status.WAITING);
        List<Booking> bookingList = bookingRepository.getAllBookingByItem(List.of(item.getId()),
                Status.WAITING, Sort.by(Sort.Direction.DESC, "start"));
        assertEquals(1, bookingList.size());
        assertEquals(booking, bookingList.get(0));
    }


}
