package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //поиск по заказчику
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerPast(long userId, LocalDateTime end);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByBookerFuture(long userId, LocalDateTime start);

    //для поиска WAITING и REJECTED
    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.booker.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByBookerAndStatus(long userId, Status status);

    //поиск по владельцу
    List<Booking> findByItemUserIdOrderByStartDesc(long ownerId);

    @Query("select booking from Booking booking " +
            "where booking.start < ?2 " +
            "and booking.end > ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.start")
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime end);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.start desc")
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime start);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.item.user.id = ?1 " +
            "order by booking.status desc")
    List<Booking> findByItemOwnerAndStatus(long userId, Status status);

    List<Booking> findAllByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);

    List<Booking> findByItemAndStatusOrderByStart(Item item, Status status);

    List<Booking> findByItemInOrderByStart(List<Item> itemList);
}
