package ru.practicum.user;

import java.util.List;

public interface UserService {
    UserDto create(UserDto dto);

    UserDto update(Long userId, UserDto dto);

    UserDto get(Long userId);

    List<UserDto> getAll();

    void delete(Long id);
}