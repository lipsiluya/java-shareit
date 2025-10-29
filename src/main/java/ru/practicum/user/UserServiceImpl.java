package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto create(UserDto dto) {
        log.info("Creating user with email: {}", dto.getEmail());
        try {
            validateCreate(dto);

            repository.findByEmail(dto.getEmail())
                    .ifPresent(u -> {
                        throw new ConflictException("Email already exists");
                    });

            User user = UserMapper.toUser(dto);
            User saved = repository.save(user);
            log.info("Created user successfully: {}", saved.getId());
            UserDto result = UserMapper.toDto(saved);
            log.info("UserDto to return: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error creating user", e);
            throw e;
        }
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        log.info("=== USER UPDATE STARTED ===");
        log.info("Updating user ID: {}, DTO: {}", id, dto);

        try {
            if (id == null || id <= 0) {
                throw new ValidationException("Invalid user ID");
            }

            User user = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

            log.info("Found user: {}", user);

            // Обновление email с проверкой уникальности
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                if (!dto.getEmail().equals(user.getEmail())) {
                    repository.findByEmail(dto.getEmail())
                            .ifPresent(u -> {
                                throw new ConflictException("Email already exists");
                            });
                    user.setEmail(dto.getEmail());
                    log.info("Updated email to: {}", dto.getEmail());
                }
            }

            // Обновление имени
            if (dto.getName() != null && !dto.getName().isBlank()) {
                user.setName(dto.getName());
                log.info("Updated name to: {}", dto.getName());
            }

            User updated = repository.save(user);
            UserDto result = UserMapper.toDto(updated);
            log.info("=== USER UPDATE SUCCESS: {} ===", result);
            return result;
        } catch (Exception e) {
            log.error("=== USER UPDATE FAILED ===", e);
            throw e;
        }
    }

    @Override
    public UserDto get(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid user ID");
        }

        return repository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid user ID");
        }

        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("User not found with id: " + id);
        }

        repository.delete(id);
        log.info("User deleted: {}", id);
    }

    private void validateCreate(UserDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ValidationException("Email is required");
        }
        if (!dto.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Name is required");
        }
    }
}