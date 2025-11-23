package com.dinar.spring_app.service.impl;

import com.dinar.spring_app.database.entity.User;
import com.dinar.spring_app.database.entity.enums.UserStatus;
import com.dinar.spring_app.database.repository.UserRepository;
import com.dinar.spring_app.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor()
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByUserName(String username) {
        Optional<User> result = userRepository.findByUsername(username);
        if (result.isPresent()) {
            log.info("IN findByUserName - user: " +
                    "{} found by username: {}", result.get().getUsername(), username);
        } else {
            log.debug("User not found by userName: {}", username);
        }
        return result;
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()) {
            log.info("IN findById - user: {} found by userId: {}", result.get().getId(), id);
        } else {
            log.debug("User not found by userId: {}", id);
        }
        return result;
    }

    @Override
    public List<User> findAll() {
        List<User> result = userRepository.findAll();
        if (result.isEmpty()) {
            log.info("IN findAll - no users found");
        } else {
            log.info("IN findAll - found {} users", result.size());
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id {} deleted", id);
    }

    @Override
    @Transactional
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        User registeredUser = userRepository.save(user);

        log.info("IN register - user: {} successfully registered", registeredUser);
        return registeredUser;
    }

    @Override
    public boolean existById(Long id) {
        return userRepository.existsById(id);
    }
}
