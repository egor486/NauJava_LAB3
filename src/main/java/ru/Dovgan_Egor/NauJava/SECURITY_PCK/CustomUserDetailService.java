package ru.Dovgan_Egor.NauJava.SECURITY_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        /*List<User> users = userRepository.findByLogin(username);

        if(users.isEmpty()){
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }

        User user = users.get(0); */

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь не найден: " + username
                ));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.singleton(authority)
        );
    }
}
