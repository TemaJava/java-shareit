package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //поиск по заказчику
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now, Pageable pagination);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerPast(long userId, LocalDateTime end, Pageable pagination);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerFuture(long userId, LocalDateTime start, Pageable pagination);

    //для поиска WAITING и REJECTED
    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByBookerAndStatus(long userId, Status status, Pageable pagination);

    //поиск по владельцу
    List<Booking> findByItemUserIdOrderByStartDesc(long ownerId, Pageable pagination);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.start")
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now, Pageable pagination);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime end, Pageable pagination);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime start, Pageable pagination);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByItemOwnerAndStatus(long userId, Status status, Pageable pagination);

    List<Booking> findAllByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);

    @Query("select booking from Booking booking " +
            "where booking.item.id in (?1) " +
            "and booking.status = ?2 ")
    List<Booking> getAllBookingByItem(List<Long> itemIds, Status status, Sort sort);
}
