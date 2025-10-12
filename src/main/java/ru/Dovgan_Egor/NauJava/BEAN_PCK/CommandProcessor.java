package ru.Dovgan_Egor.NauJava.BEAN_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.Dovgan_Egor.NauJava.SERVICE_PCK.TaskService;

@Component
public class CommandProcessor {
    private final TaskService taskService;

    @Autowired
    public CommandProcessor(TaskService taskService){
        this.taskService = taskService;
    }

    public void processCommand(String input)
    {
        String[] cmd = input.split(" ");
        switch (cmd[0]){
            case "create" ->
            {
                taskService.createTask(Long.valueOf(cmd[1]),cmd[2], cmd[3], Boolean.valueOf(cmd[4]));
                System.out.println("Задача была добавлена");
            }
            case "find" ->
            {
                var id = taskService.findById(Long.valueOf(cmd[1]));
                if ( id != null ) {
                    System.out.println("Задача с " + id + " была найдена");
                }
                else {
                    System.out.println("Задача с введенным id не была найдена");
                }
            }
            case "delete" ->
            {
                taskService.deleteTask(Long.valueOf(cmd[1]));
                System.out.println("Задача была удалена");
            }
            case "update" ->
            {
                taskService.updateStatus(Long.valueOf(cmd[1]),Boolean.valueOf(cmd[2]));
                System.out.println("Статус задачи был изменен");
            }
            default -> System.out.println("Введена неизвестная команда...");
        }
    }
}
