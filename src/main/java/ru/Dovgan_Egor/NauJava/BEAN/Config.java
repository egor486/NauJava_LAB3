package ru.Dovgan_Egor.NauJava.BEAN;

import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {
    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public List<Task> taskContainter (){
        return new ArrayList<>();
    }
}
