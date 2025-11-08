package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        validateUser(userDto);

        // Проверка уникальности email
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ConflictException("User with email " + userDto.getEmail() + " already exists");
        }

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        // Обновляем поля, если они предоставлены
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            // Проверяем, что email не занят другим пользователем
            userRepository.findByEmail(userDto.getEmail())
                    .ifPresent(user -> {
                        if (!user.getId().equals(userId)) {
                            throw new ConflictException("Email " + userDto.getEmail() + " is already taken");
                        }
                    });
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email cannot be blank");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Email must contain @");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Name cannot be blank");
        }
    }
}