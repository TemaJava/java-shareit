package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
        @NotNull
        private Long id;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BookingItemDto {
        @NotNull
        private Long id;
        @NotBlank
        private String name;
    }
}
