package com.dinar.spring_app.rest;

import com.dinar.spring_app.database.entity.User;
import com.dinar.spring_app.security.annotation.IsAdmin;
import com.dinar.spring_app.security.annotation.IsModerator;
import com.dinar.spring_app.security.annotation.IsUser;
import com.dinar.spring_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.dinar.spring_app.utill.SecurityUtils.getCurrentUserFromSecurity;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @IsUser
    public ResponseEntity<User> getCurrentUser() {
        var currentUser = getCurrentUserFromSecurity();
        var user = userService.findById(currentUser.getId())
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "id/{id}")
    @IsModerator
    public ResponseEntity<User> getUserById(@PathVariable("id") Long userId) {
        var user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(user);
    }

    @GetMapping(value = "username/{username}")
    @IsModerator
    public ResponseEntity<User> getUserByName(@PathVariable String username) {
        var user = userService.findByUserName(username)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @IsAdmin
    public ResponseEntity<List<User>> getAll() {
        var users = userService.findAll();
        return users.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(users);
    }

    @DeleteMapping(value = "id/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long userId) {
        userService.findById(userId)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userService.deleteById(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user) {
        var savedUser = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
