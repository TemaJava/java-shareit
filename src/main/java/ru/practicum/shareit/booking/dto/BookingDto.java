package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;


import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDto {
    @NotNull(groups = Update.class)
    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;
    @NotNull(groups = Create.class)
    private Long itemId;
}
