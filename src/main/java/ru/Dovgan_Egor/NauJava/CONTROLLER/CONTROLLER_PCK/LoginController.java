package ru.Dovgan_Egor.NauJava.CONTROLLER.CONTROLLER_PCK;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // имя Thymeleaf шаблона login.html
    }
}