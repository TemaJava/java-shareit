package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ShortBookingDtoToResponse;


import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ShortBookingDtoToResponse lastBooking;
    private ShortBookingDtoToResponse nextBooking;
    private List<CommentDtoToResponse> comments;
}
