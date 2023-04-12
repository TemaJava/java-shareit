package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);
    List<ItemDto> getAllUsersItems(long userId);
    ItemDto getItemById(long id);
    ItemDto updateItem(long userId, long itemId, Item item);
    List<ItemDto> findItem(String string);
}
