package ru.Dovgan_Egor.NauJava.CONTROLLER.CONTROLLER_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.SERVICE.UserService;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        try {
            user.setRole("USER");
            userService.addUser(user);
            return "redirect:/login";
        } catch (Exception ex) {
            model.addAttribute("message", "Пользователь с таким именем или логином уже существует");
            return "registration";
        }
    }
}