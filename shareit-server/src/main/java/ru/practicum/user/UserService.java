package ru.practicum.user;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);

    UserDto getById(Long userId);

    List<UserDto> getAll();
}