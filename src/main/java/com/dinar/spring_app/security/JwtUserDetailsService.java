package com.dinar.spring_app.security;

import com.dinar.spring_app.security.jwt.JwtUser;
import com.dinar.spring_app.security.jwt.JwtUserFactory;
import com.dinar.spring_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userService.findByUserName(username);

        var jwtUser = user.map(JwtUserFactory::create)
                          .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);
        return jwtUser;
    }
}
