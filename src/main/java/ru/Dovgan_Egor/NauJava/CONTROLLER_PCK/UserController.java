package ru.Dovgan_Egor.NauJava.CONTROLLER_PCK;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/add")
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }


    @GetMapping("/findByName/{name}")
    public List<User> findByName(@PathVariable String name) {
        return userService.getUserByName(name);
    }


    @GetMapping("/all")
    public Iterable<User> getAll() {
        return userService.getAllUsers();
    }

}
