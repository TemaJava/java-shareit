package ru.practicum.shareit.itemTests.itemControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoToResponse;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private UserDto userDto;

    private UserDto anotherUserDto;

    private ItemDto itemDto;

    private CommentDtoToResponse commentDtoToResponse;

    private ItemBookingDto itemBookingDto;

    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        User user = new User(1L, "User name", "user@mail.com");
        userDto = UserMapper.toUserDto(user);

        User anotherUser = new User(2L, "Another name", "another@mail.com");
        anotherUserDto = UserMapper.toUserDto(anotherUser);

        Item item = new Item(1L, "itemName", "itemDesc", true, user, null);
        itemDto = ItemMapper.createItemDto(item);
        itemBookingDto = ItemMapper.toItemDtoBooking(item, null);

        Comment comment = new Comment(1L, "commentText", item, anotherUser, now);
        commentDtoToResponse = CommentMapper.toCommentDtoToResponse(comment);
    }

    @Test
    void findAll() throws Exception {
        when(itemService.getAllUsersItems(anyLong())).thenReturn(List.of(itemBookingDto));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemBookingDto))));
    }

    @Test
    void findItem() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemBookingDto);

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemBookingDto)));
    }

    @Test
    void create() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void update() throws Exception {
        when((itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.findItem(anyString())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item1")
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDtoToResponse);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoToResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDtoToResponse)));
    }
}
