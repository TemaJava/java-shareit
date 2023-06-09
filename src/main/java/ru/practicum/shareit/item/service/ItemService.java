package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoToResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    List<ItemBookingDto> getAllUsersItems(long userId);

    ItemBookingDto getItemById(long userId, long itemId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> findItem(String string);

    CommentDtoToResponse addComment(long userId, long itemId, CommentDto commentDto);
}
