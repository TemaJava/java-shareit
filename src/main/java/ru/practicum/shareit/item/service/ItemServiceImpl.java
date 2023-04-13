package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor=@__({@Autowired}))
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
        return ItemMapper.createItemDto(itemStorage.createItem(userId, ItemMapper.createItem(itemDto, userId)));
    }

    @Override
    public List<ItemDto> getAllUsersItems(long userId) {
        userStorage.checkIsExist(userId);
        return itemStorage.getAllItems(userId).stream().map(ItemMapper::createItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id) {
        itemStorage.checkItemExist(id);
        return ItemMapper.createItemDto(itemStorage.findItemById(id));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        userStorage.checkIsExist(userId);
        itemStorage.checkItemExist(itemId);
        itemStorage.checkItemByUser(userId);
        return ItemMapper.createItemDto(itemStorage.updateItem(userId, itemId, item));
    }

    @Override
    public List<ItemDto> findItem(String string) {
        if (string.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemStorage.findItem(string).stream().map(ItemMapper::createItemDto).collect(Collectors.toList());
        }
    }
}
