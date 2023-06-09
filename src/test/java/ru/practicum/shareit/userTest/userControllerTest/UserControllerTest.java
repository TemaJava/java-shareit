package ru.practicum.shareit.userTest.userControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserServiceImpl userService;

    private UserDto userDto;
    private UserDto userAnotherDto;

    @BeforeEach
    void createUserDto() {
        userDto = UserMapper.toUserDto(new User(1L, "User test name", "userTestMail@mail.com"));
        userAnotherDto = UserMapper.toUserDto(new User(2L, "User Another test name",
                "userAnotherTestMail@mail.com"));
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @Test
    void getAllUserTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto, userAnotherDto));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto, userAnotherDto))));
    }


    @Test
    void getAllWithoutCreatingUsersShouldReturnEmptyListTest() throws Exception {
        List<User> users = Collections.emptyList();

        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(users)));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @Test
    void getUserByWrongIdShouldReturnNotFoundStatusTest() throws Exception {
        long userId = 100;
        when(userService.getUserById(userId)).thenThrow(new ObjectNotFoundException("Пользователь с id "
                + userId + " не обнаружен"));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @Test
    void deleteUserTest() throws Exception {
        when(userService.deleteUser(anyLong())).thenReturn(userDto);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @Test
    void deleteWithoutCreatingUserShouldReturnNotFound() throws Exception {
        long userId = 1;

        when(userService.deleteUser(userId)).thenThrow(new ObjectNotFoundException("Пользователь с id "
                + userId + " не обнаружен"));
        mockMvc.perform(delete("/users/" + userId)).andExpect(status().isNotFound());
    }
}
