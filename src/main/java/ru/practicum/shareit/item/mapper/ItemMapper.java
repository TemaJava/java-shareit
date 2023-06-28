package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.ShortBookingDtoToResponse;
import ru.practicum.shareit.item.dto.CommentDtoToResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDto createItemDto(Item item) {
        Long requestId = null;
        if (item.getRequest() != null) requestId = item.getRequest().getId();
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), requestId
        );
    }

    public Item createItem(ItemDto itemDto, User user, Request request) {
        return new Item(itemDto.getId(), itemDto.getName(),
                itemDto.getDescription(), itemDto.getAvailable(),
                user, request);
    }

    public ItemBookingDto toItemDtoBooking(ItemBookingDto itemBookingDto, List<CommentDtoToResponse> commentList) {
        return new ItemBookingDto(itemBookingDto.getId(),
                itemBookingDto.getName(),
                itemBookingDto.getDescription(),
                itemBookingDto.getAvailable(),
                itemBookingDto.getLastBooking(),
                itemBookingDto.getNextBooking(),
                commentList);
    }

    public ItemBookingDto toItemDtoBooking(Item item, List<CommentDtoToResponse> list) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                list
        );
    }

    public ItemBookingDto toItemDtoBooking(Item item, ShortBookingDtoToResponse nextBookingShort,
                                           ShortBookingDtoToResponse lastBookingShort) {
        return new ItemBookingDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingShort,
                nextBookingShort,
                new ArrayList<>());
    }
}
