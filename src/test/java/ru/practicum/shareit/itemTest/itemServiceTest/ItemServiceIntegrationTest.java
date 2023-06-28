package ru.practicum.shareit.itemTest.itemServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getAllItemsTest() {

        User user = new User(1L, "User name", "user@mail.com");
        UserDto userDto = UserMapper.toUserDto(user);
        long userId = userService.createUser(userDto).getId();

        UserDto userOut = userService.getUserById(userId);

        Item item = new Item(1L, "itemName", "itemDesc", true, user, null);
        ItemDto itemDto = ItemMapper.createItemDto(item);

        itemService.createItem(userId, itemDto);

        ItemBookingDto itemOut = itemService.getItemById(1L, 1L);

        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(item.getName()));
        assertThat(itemOut.getDescription(), equalTo(item.getDescription()));

        List<ItemBookingDto> items = itemService.getAllUsersItems(userOut.getId());

        assertThat(items.size(), equalTo(1));
    }
}
