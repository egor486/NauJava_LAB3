package ru.Dovgan_Egor.NauJava.CONTROLLER_PCK;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.REPOSITORY_PCK.TaskRestRepository;

import java.util.List;

@Controller
public class TaskPageController {

    private final TaskRestRepository taskRepository;

    public TaskPageController(TaskRestRepository  taskRepository){
        this.taskRepository = taskRepository;
    }

    @GetMapping("/tasks-page")
    public String tasksPage(Model model){
        List<Task> tasks = taskRepository.findAll();
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
}
