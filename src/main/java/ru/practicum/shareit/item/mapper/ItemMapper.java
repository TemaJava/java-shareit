package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDto createItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable()
        );
    }

    public Item createItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                user);
    }

    public ItemBookingDto toItemDtoBooking(Item item) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>()
        );
    }

    public ItemBookingDto toItemDtoBooking(Item item, List<CommentDto> list) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                list
        );
    }

    public ItemBookingDto toItemDtoBooking(Item item, BookingDto nextBookingShort,
                                                  BookingDto lastBookingShort, List<CommentDto> itemComments) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingShort,
                nextBookingShort,
                itemComments);
    }

    public ItemBookingDto toItemDtoBooking(Item item, BookingDto nextBookingShort,
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
