package system.audit.security;


import lombok.AllArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import system.audit.dto.User;
import system.audit.repositories.UserRepository;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return convertUserToUserDetails(user);
    }

    private UserDetails convertUserToUserDetails(User user) {
        final var list = new ArrayList<SimpleGrantedAuthority>();
        final var roleName  = user.getRole().name();
        final var authority = new SimpleGrantedAuthority(roleName);

        list.add(authority);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                list
        );
    }

}
