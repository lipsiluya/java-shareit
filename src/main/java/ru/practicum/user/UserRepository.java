package ru.practicum.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    void delete(Long id);
}