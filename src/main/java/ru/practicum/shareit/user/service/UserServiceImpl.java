package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь с id = " + id + " не найден");
        });
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, User newUser) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь с id = " + id + " для обновления не найден");
        });
        if (newUser.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            checkIfEmailExists(newUser.getEmail());
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto deleteUser(long id) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException("Пользователь с id = " + id + " для обновления не найден");
        });
        repository.delete(user);
        return UserMapper.toUserDto(user);
    }

    private void checkIfEmailExists(String email) {
        repository.findByEmail(email).ifPresent(user -> {
            throw new ValidationException(email);
        });
    }
}