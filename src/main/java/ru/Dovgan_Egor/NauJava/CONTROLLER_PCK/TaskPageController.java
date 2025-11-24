package ru.Dovgan_Egor.NauJava.CONTROLLER_PCK;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.SubTaskRepository;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.TaskStatusRepository;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.SubTask;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.TaskStatus;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.REPOSITORY_PCK.TaskRestRepository;
import java.util.List;

@Controller
public class TaskPageController {

    private final TaskRestRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final SubTaskRepository subTaskRepository;

    public TaskPageController(TaskRestRepository  taskRepository,
                              UserRepository userRepository,
                              TaskStatusRepository taskStatusRepository,
                              SubTaskRepository subTaskRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.subTaskRepository = subTaskRepository;
    }

    // Визуализация всех задач по пользователю
    @GetMapping("/tasks-page")
    public String tasksPage(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<Task> tasks = taskRepository.findTasksByUserLogin(username);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    // Для добавления новой задачи
    @GetMapping("/tasks/add")
    public String addTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "create-task";
    }

    @PostMapping("/tasks/add")
    public String saveTask(@ModelAttribute("task") Task task) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskStatus defaultStatus = taskStatusRepository.findByName("НОВАЯ")
                .orElseThrow(() -> new RuntimeException("Status НОВАЯ not found"));

        task.setUser_id(user);
        task.setStatus_id(defaultStatus);

        taskRepository.save(task);

        return "redirect:/tasks-page";
    }

    // Для редактирования задачи
    @GetMapping("/tasks/edit/{id}")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        List<SubTask> subTasks = subTaskRepository.findByTaskId(id);

        model.addAttribute("task", task);

        // для добавления подзадач
        model.addAttribute("subTasks", subTasks);
        model.addAttribute("newSubTask", new SubTask());

        // для работы со статусами
        //List<TaskStatus> allStatuses = (List<TaskStatus>) taskStatusRepository.findAll();
        List<TaskStatus> allStatuses = (List<TaskStatus>) taskStatusRepository.findStatusEditUser();
        model.addAttribute("allStatuses", allStatuses);

        return "edit-task";
    }


    @PostMapping("/tasks/edit/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute("task") Task updatedTask) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Обновляем только изменяемые поля
        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setDt_beg(updatedTask.getDt_beg());
        task.setDt_end(updatedTask.getDt_end());
        task.setStatus_id(updatedTask.getStatus_id());

        taskRepository.save(task);

        return "redirect:/tasks-page";
    }

    // Для удаления задачи
    @PostMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id) {

        taskRepository.deleteById(id);

        return "redirect:/tasks-page";
    }

    // Для добавления подзадач
    @PostMapping("/tasks/{taskId}/subtasks/add")
    public String addSubTask(
            @PathVariable Long taskId,
            @ModelAttribute("newSubTask") SubTask subTask
    )
    {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        subTask.setTask_id(task);
        subTask.setIs_completed(false); // по умолчанию новая подзадача не выполнена

        subTaskRepository.save(subTask);

        return "redirect:/tasks/edit/" + taskId;
    }

    @PostMapping("/subtasks/{subTaskId}/toggle")
    public String toggleSubTask(@PathVariable Long subTaskId) {

        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("SubTask not found"));

        // Переключение статуса
        boolean newStatus = !Boolean.TRUE.equals(subTask.getIs_completed());
        subTask.setIs_completed(newStatus);
        subTaskRepository.save(subTask);

        Long taskId = subTask.getTask_id().getId();

        return "redirect:/tasks/edit/" + taskId;
    }

}
