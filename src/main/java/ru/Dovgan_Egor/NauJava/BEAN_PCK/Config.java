package ru.Dovgan_Egor.NauJava.BEAN_PCK;

import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {

    /*@Bean
    public CommandLineRunner commandScanner() {
        return args ->{
            try (Scanner scanner = new Scanner(System.in)){
                System.out.println("введите команду 'exit' для выхода");
                while(true){
                    System.out.print("> ");
                    String input = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(input.trim()))
                    {
                        System.out.println("Выход из программы...");
                        break;
                    }
                    commandProcessor.processCommand(input);
                }
            }
        };
    }*/
    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public List<Task> taskContainter (){
        return new ArrayList<>();
    }
}
