package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.validation.BookingCreate;
import ru.practicum.shareit.booking.validation.BookingUpdate;


import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDto {
    @NotNull(groups = BookingUpdate.class)
    private Long id;
    @Future
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;
    @NotNull(groups = BookingCreate.class)
    private Long itemId;
    private Long bookerId;
    private Status status;
}
