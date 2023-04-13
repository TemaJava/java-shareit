package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

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

    public List<User> getAllUsers() {
        return new ArrayList<>(usersMap.values());
    }

    public User getUserById(long id) {
        return usersMap.get(id);
    }

    public User createUser(User user) {
        checkMail(user);
        long newId = generatedId++;
        user.setId(newId);
        usersMap.put(newId, user);
        return user;
    }

    public User updateUser(long id, User user) {
        if (user.getName() != null) {
            usersMap.get(id).setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkMail(user, id);
            usersMap.get(id).setEmail(user.getEmail());
        }
        return usersMap.get(id);
    }

    public User deleteUser(long id) {
        User answUser = usersMap.get(id);
        usersMap.remove(id);
        return answUser;
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
