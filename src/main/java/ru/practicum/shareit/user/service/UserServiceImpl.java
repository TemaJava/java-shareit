package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl() {
        this.repository = new UserRepository();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.getAllUsers();
    }

    @Override
    public UserDto getUserById(long id) {
        return repository.getUserById(id);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return repository.createUser(userDto);
    }

    @Override
    public User updateUser(long id, User user) {
        return repository.updateUser(id, user);
    }

    @Override
    public UserDto deleteUser(long id) {
        return repository.deleteUser(id);
    }
}
