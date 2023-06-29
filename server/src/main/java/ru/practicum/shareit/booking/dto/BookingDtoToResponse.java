package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;

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
    private BookingItemDto item;
    private BookingBookerDto booker;
    private Status status;

    //внутренний класс букера для построения букингДто
    @Getter
    @Setter
    @AllArgsConstructor
    public static class BookingBookerDto {
        private Long id;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BookingItemDto {
        private Long id;
        private String name;
    }
}
