package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepository {
    //List<Item> на случай если одному пользователю принадлежат несколько итемов
    Map<Long, List<Item>> itemsByUser;
    Map<Long, Item> itemById;
    private long generatedId;

    public ItemRepository() {
        this.itemsByUser = new HashMap<>();
        this.itemById = new HashMap<>();
        this.generatedId = 1;
    }

    public List<ItemDto> getAllItems(long userId) {
        List<Item> list = itemsByUser.get(userId);
        if (list.size() != 0) {
            log.info("отправка списка item пользователя с id - {}", userId);
            return list.stream().map(ItemMapper::createItemDto).collect(Collectors.toList());
        } else {
            log.warn("пользователь с id {}", userId);
            throw new ObjectNotFoundException("Пользователь с таким id не найден");
        }
    }

    public ItemDto findItemById(long itemId) {
        Item item = itemById.get(itemId);
        if (item != null) {
            log.info("Найден item c id {}", itemId);
            return ItemMapper.createItemDto(item);
        } else {
            throw new ObjectNotFoundException("Item с таким id не найден");
        }
    }

    public ItemDto createItem(long userId, ItemDto itemDto) {
        long newId = generatedId++;
        itemDto.setId(newId);
        Item item = ItemMapper.createItem(itemDto, userId);
        if (itemsByUser.containsKey(userId)) {
            itemsByUser.get(userId).add(item);
        } else {
            List<Item> newList = new ArrayList<>();
            newList.add(item);
            itemsByUser.put(userId, newList);
        }
        itemById.put(newId, item);
        return  ItemMapper.createItemDto(item); //доп проверка
    }

    public ItemDto updateItem(long userId, long itemId, Item item) {
        if (!itemsByUser.containsKey(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        Item updatingItem = itemById.get(itemId);
        if (item != null) {
            log.info("Найден item c id {}", itemId);
        } else {
            throw new ObjectNotFoundException("Item с таким id не найден");
        }

        if (item.getName() != null) {
            updatingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatingItem.setAvailable(item.getAvailable());
        }
        //пользователь не обновляется

        for (Item userItem : itemsByUser.get(userId)) {
            if (userItem.getId() == itemId) {
                itemsByUser.get(userId).remove(userItem);
                itemsByUser.get(userId).add(updatingItem);
            }
        }
        itemById.replace(updatingItem.getId(), updatingItem);
        return ItemMapper.createItemDto(updatingItem);
    }

    public List<ItemDto> findItem(String string) {
        List<ItemDto> itemsToResponse = new ArrayList<>();
        for (Item item : itemById.values()) {
            if (item.getName().toLowerCase().contains(string.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(string.toLowerCase())) {
                if (item.getAvailable()) {
                    itemsToResponse.add(ItemMapper.createItemDto(item));
                }
            }
        }
        return itemsToResponse;
    }
}
