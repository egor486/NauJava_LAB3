package ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.EXCEPTION_PCK.UserNotFoundException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser (User user){
        user.setCreated_at(Timestamp.from(Instant.now()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getUserByName(String name){
        return userRepository.findByName(name);
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id)
                .or(() -> {
                    throw new UserNotFoundException("User not found: " + id);
                });
    }

    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }
}
