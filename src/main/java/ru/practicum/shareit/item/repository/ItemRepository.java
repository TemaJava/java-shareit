package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;


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

    public List<Item> getAllItems(long userId) {
        List<Item> list = itemsByUser.get(userId);
        log.info("отправка списка item пользователя с id - {}", userId);
        return list;
    }

    public Item findItemById(long itemId) {
        return itemById.get(itemId);
    }

    public Item createItem(long userId, Item item) {
        long newId = generatedId++;
        item.setId(newId);
        if (itemsByUser.containsKey(userId)) {
            itemsByUser.get(userId).add(item);
        } else {
            List<Item> newList = new ArrayList<>();
            newList.add(item);
            itemsByUser.put(userId, newList);
        }
        itemById.put(newId, item);
        return item; //доп проверка
    }

    public Item updateItem(long userId, long itemId, Item item) {
        Item updatingItem = itemById.get(itemId);
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
        return updatingItem;
    }

    public List<Item> findItem(String string) {
        List<Item> itemsToResponse = new ArrayList<>();
        for (Item item : itemById.values()) {
            if (item.getName().toLowerCase().contains(string.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(string.toLowerCase())) {
                if (item.getAvailable()) {
                    itemsToResponse.add(item);
                }
            }
        }
        return itemsToResponse;
    }

    public void checkItemExist(long itemId) {
        Item item = itemById.get(itemId);
        if (item != null) {
            log.info("Найден item c id {}", itemId);
        } else {
            throw new ObjectNotFoundException("Item с таким id не найден");
        }
    }

    public void checkItemByUser(long userId) {
        if (!itemsByUser.containsKey(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }
}
