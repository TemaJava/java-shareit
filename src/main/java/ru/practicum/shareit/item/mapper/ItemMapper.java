package ru.practicum.shareit.item.mapper;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemMapper {
    public static ItemDto createItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable()
        );
    }

    public static Item createItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                user);
    }

    public static ItemBookingDto toItemDtoBooking(Item item) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>()
        );
    }

    public static ItemBookingDto toItemDtoBooking(Item item, List<CommentDto> list) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                list
        );
    }

    public static ItemBookingDto toItemDtoBooking(Item item, BookingDto nextBookingShort,
                                                  BookingDto lastBookingShort, List<CommentDto> itemComments) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingShort,
                nextBookingShort,
                itemComments);
    }

    public static ItemBookingDto toItemDtoBooking(Item item, BookingDto nextBookingShort,
                                                  BookingDto lastBookingShort) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingShort,
                nextBookingShort,
                new ArrayList<>());
    }
}
