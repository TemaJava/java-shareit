package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;

    public ItemServiceImpl() {
        this.itemStorage = new ItemRepository();
        this.userStorage = new UserRepository();
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userStorage.checkIsExist(userId);
        return itemStorage.createItem(userId, itemDto);
    }

    @Override
    public List<ItemDto> getAllUsersItems(long userId) {
        userStorage.checkIsExist(userId);
        return itemStorage.getAllItems(userId);
    }

    @Override
    public ItemDto getItemById(long id) {
        return itemStorage.findItemById(id);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        userStorage.checkIsExist(userId);
        return itemStorage.updateItem(userId, itemId, item);
    }

    @Override
    public List<ItemDto> findItem(String string) {
        if (string.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemStorage.findItem(string);
        }
    }
}
