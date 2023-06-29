package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoToResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto createItem(long userId, ItemDto itemDto);

    public List<ItemBookingDto> getAllUsersItems(long userId);

    public ItemBookingDto getItemById(long userId, long itemId);

    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    public List<ItemDto> findItem(String string);

    public CommentDtoToResponse addComment(long userId, long itemId, CommentDto commentDto);
}