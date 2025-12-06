package ru.Dovgan_Egor.NauJava.BEAN;

import org.springframework.boot.CommandLineRunner;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskStatusRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {
    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public List<Task> taskContainter (){
        return new ArrayList<>();
    }

    @Bean
    public CommandLineRunner seedTaskStatuses(TaskStatusRepository repository) {
        return args -> {
            String[] statuses = {"НОВАЯ", "В ПРОГРЕССЕ", "ЗАВЕРШЕНА"};
            for (String statusName : statuses) {
                repository.findByName(statusName)
                        .orElseGet(() -> repository.save(new TaskStatus(null, statusName)));
            }
        };
    }
}
