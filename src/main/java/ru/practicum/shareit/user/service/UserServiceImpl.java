package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor=@__({@Autowired}))
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl() {
        this.repository = new UserRepository();
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = new ArrayList<>(repository.getAllUsers());
        List<UserDto> responseList = new ArrayList<>();
        for (User user : userList) {
            responseList.add(UserMapper.toUserDto(user));
        }
        return responseList;
    }

    @Override
    public UserDto getUserById(long id) {
        repository.checkIsExist(id);
        return UserMapper.toUserDto(repository.getUserById(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(repository.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public User updateUser(long id, User user) {
        repository.checkIsExist(id);
        return repository.updateUser(id, user);
    }

    @Override
    public UserDto deleteUser(long id) {
        repository.checkIsExist(id);
        return UserMapper.toUserDto(repository.deleteUser(id));
    }
}
