package com.dinar.spring_app.security.jwt;

import com.dinar.spring_app.database.entity.Role;
import com.dinar.spring_app.database.entity.User;
import com.dinar.spring_app.database.entity.enums.UserStatus;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getStatus().equals(UserStatus.ACTIVE),
                user.getUpdated(),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles()))
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
        return userRoles.stream()
                .map(role ->  new SimpleGrantedAuthority(role.getName())
                ).collect(Collectors.toList());
    }
}
