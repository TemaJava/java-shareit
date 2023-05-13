package ru.practicum.shareit.userTests.userServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ru.practicum.shareit.user.service.UserServiceImpl userService;

    private User user;
    private User anotherUser;

    @BeforeEach
    void createUser() {
        user = new User(1L, "User test name", "Usertest@mail.com");
        anotherUser = new User(2L, "AnotherUser test name", "AnotherEmail@mail.ru");
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto userTestDto = userService.createUser(UserMapper.toUserDto(user));
        assertEquals(1, userTestDto.getId());
        assertEquals("User test name", userTestDto.getName());
        assertEquals("Usertest@mail.com", userTestDto.getEmail());
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.getUserById(user.getId());
        assertEquals(1, userDto.getId());
        assertEquals("User test name", userDto.getName());
        assertEquals("Usertest@mail.com", userDto.getEmail());
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user, anotherUser));

        List<UserDto> userList = userService.getAllUsers();
        assertEquals(2, userList.size());
        assertEquals(1, userList.get(0).getId());
        assertEquals("User test name", userList.get(0).getName());
        assertEquals(2, userList.get(1).getId());
        assertEquals("AnotherEmail@mail.ru", userList.get(1).getEmail());
    }

    @Test
    void getUserWithWrongIdThrowsExceptionTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userService.getUserById(user.getId()));
        assertEquals("Пользователь с id = " + 1 + " не найден", exception.getMessage());
    }

    @Test
    void updateUserTest() {
        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        userDto.setEmail("NewMail@User.ru");
        assertThat(userService.updateUser(userDto.getId(), userDto)).isEqualTo(userDto);
    }

    @Test
    void deleteUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.deleteUser(user.getId());
        assertEquals(1, userDto.getId());
        assertEquals("User test name", userDto.getName());
        assertEquals("Usertest@mail.com", userDto.getEmail());
    }

    @Test
    void deleteUserWithNoUserThrowsExceptionTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDto userDto = UserMapper.toUserDto(user);
        userDto.setId(999);
        ObjectNotFoundException exc = assertThrows(ObjectNotFoundException.class,
                () -> userService.deleteUser(999)
        );
        assertEquals("Пользователь с id = " + 999 + " для обновления не найден", exc.getMessage());
    }
}
