package ru.Dovgan_Egor.NauJava.CONTROLLER_PCK;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<Task> tasks = taskRepository.findTasksByUserLogin(username);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
}
