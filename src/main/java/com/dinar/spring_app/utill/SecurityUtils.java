package com.dinar.spring_app.utill;

import com.dinar.spring_app.database.entity.User;
import com.dinar.spring_app.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SecurityUtils {

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        return authentication.getName();
    }

    public static User getCurrentUser(UserService userService) {
        String username = getCurrentUsername();
        return userService.findByUserName(username)
                          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
