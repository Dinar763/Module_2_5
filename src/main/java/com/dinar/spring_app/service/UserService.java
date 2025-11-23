package com.dinar.spring_app.service;

import com.dinar.spring_app.database.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends GenericService<User> {

    Optional<User> findByUserName(String username);
    User register(User user);
}
