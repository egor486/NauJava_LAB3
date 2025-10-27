package ru.Dovgan_Egor.NauJava.CONFIG_PCK;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task To-Do LIST API")
                        .version("1.0")
                        .description("REST API для управления задачами"));
    }
}