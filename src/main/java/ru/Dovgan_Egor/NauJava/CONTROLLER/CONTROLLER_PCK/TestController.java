package ru.Dovgan_Egor.NauJava.CONTROLLER.CONTROLLER_PCK;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


public class TestController {
    @GetMapping("/test-swagger")
    public Map<String, String> test() {
        return Map.of("msg", "Hello Swagger");
    }
}
