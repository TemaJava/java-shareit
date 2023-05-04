package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDtoToResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;
}
