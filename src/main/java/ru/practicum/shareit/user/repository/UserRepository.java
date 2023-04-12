package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Long, User> usersMap;
    private int generatedId;

    public UserRepository() {
        this.usersMap = new HashMap<>();
        this.generatedId = 1;
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> responseList = new ArrayList<>();
        for (User user : usersMap.values()) {
            responseList.add(UserMapper.toUserDto(user));
        }
        return responseList;
    }

    public UserDto getUserById(long id) {
        if (usersMap.containsKey(id)) {
            return UserMapper.toUserDto(usersMap.get(id));
        } else {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    public UserDto createUser(UserDto userDto) {
        checkMail(UserMapper.toUser(userDto));
        long newId = generatedId++;
        userDto.setId(newId);
        usersMap.put(newId, UserMapper.toUser(userDto));
        return userDto;
    }

    public User updateUser(long id, User user) {
        if (usersMap.containsKey(id)) {
            if (user.getName() != null) {
                usersMap.get(id).setName(user.getName());
            }
            if (user.getEmail() != null) {
                checkMail(user, id);
                usersMap.get(id).setEmail(user.getEmail());
            }
            return usersMap.get(id);
        } else {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    public UserDto deleteUser(long id) {
        if (usersMap.containsKey(id)) {
            UserDto answUser = UserMapper.toUserDto(usersMap.get(id));
            usersMap.remove(id);
            return answUser;
        } else {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    public boolean checkIsExist(long id) {
        if (usersMap.containsKey(id)) {
            return true;
        } else {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    public void checkMail(User checkUser) {
        for (User user : usersMap.values()) {
            if (checkUser.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Ошибка валидации. пользователь с таким mail уже существует");
            }
        }
    }

    public void checkMail(User checkUser, long id) {
        for (User user : usersMap.values()) {
            if (checkUser.getEmail().equals(user.getEmail()) && id != user.getId()) {
                throw new ValidationException("Ошибка валидации. пользователь с таким mail уже существует");
            }
        }
    }
}
