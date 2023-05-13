package ru.practicum.shareit.itemTests.itemRepositoryTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void search() {
        User user = new User(1L, "Name", "email@mail.ru");
        userRepository.save(user);
        Item item = new Item(1L, "itemName", "itemDesc", true, user, null);
        Item item2 = new Item(2L, "anotherName", "дрель", true, user, null);
        itemRepository.save(item);
        itemRepository.save(item2);
        List<Item> listDesc = itemRepository.findAllByString("des");
        List<Item> listName = itemRepository.findAllByString("name");


        assertEquals(1, listDesc.size());
        assertEquals(2, listName.size());
    }
}
