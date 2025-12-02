package ru.Dovgan_Egor.NauJava.CONTROLLER_PCK;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.SubTaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskStatusRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.SubTask;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.MODEL.REPOSITORY_PCK.TaskRestRepository;

import java.util.Date;
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
                .orElseThrow(() -> new RuntimeException("Пользовтель не найден"));

        TaskStatus defaultStatus = taskStatusRepository.findByName("НОВАЯ")
                .orElseThrow(() -> new RuntimeException("Статус НОВАЯ не найден"));

        task.setUser_id(user);
        task.setStatus_id(defaultStatus);

        taskRepository.save(task);

        return "redirect:/tasks-page";
    }

    // Для редактирования задачи
    @GetMapping("/tasks/edit/{id}")
    public String editTaskForm(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));

        List<SubTask> subTasks = subTaskRepository.findByTaskId(id);

        model.addAttribute("task", task);

        // для добавления подзадач
        model.addAttribute("subTasks", subTasks);
        model.addAttribute("newSubTask", new SubTask());

        // для работы со статусами
        List<TaskStatus> allStatuses = (List<TaskStatus>) taskStatusRepository.findStatusEditUser();
        model.addAttribute("allStatuses", allStatuses);

        return "edit-task";
    }


    @PostMapping("/tasks/edit/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute("task") Task updatedTask, Model model) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));

        // Реализуем логику проверки наличия незавершенных подзадач
        Long newStatusId = updatedTask.getStatus_id().getId();

        Long doneStatus = 3L;

        if (newStatusId.equals(doneStatus)){
            long countUnfinished = subTaskRepository.countByTaskIdAndCompletedFalse(id);

            if (countUnfinished > 0) {
                model.addAttribute("task", task);
                model.addAttribute("subTasks",
                        subTaskRepository.findByTaskId(id));
                model.addAttribute("newSubTask", new SubTask());
                model.addAttribute("allStatuses",
                        taskStatusRepository.findStatusEditUser());

                model.addAttribute("errorMessage",
                        "Нельзя завершить задачу: есть незавершенные подзадачи.");

                return "edit-task";
            }
        }

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
            @RequestParam("taskName") String taskName,
            @RequestParam("taskDescription") String taskDescription,
            @RequestParam("dt_beg") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dtBeg,
            @RequestParam("dt_end") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dtEnd,
            //@RequestParam("statusId") Long statusId,
            @RequestParam("subTaskName") String subTaskName
    )
    {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
        task.setName(taskName);
        task.setDescription(taskDescription);
        task.setDt_beg(dtBeg);
        task.setDt_end(dtEnd);
        //task.setStatus_id(taskStatusRepository.findById(statusId).orElseThrow(() -> new RuntimeException("Статус не найден")));
        taskRepository.save(task);

        TaskStatus inProgressStatus = taskStatusRepository.findByName("В ПРОГРЕССЕ")
                .orElseThrow(() -> new RuntimeException("Статус В ПРОГРЕССЕ не найден"));

        // Автоматически переводим задачу в статус "В ПРОГРЕССЕ" при создании новой подзадачи
        task.setStatus_id(inProgressStatus);
        taskRepository.save(task);

        SubTask subTask = new SubTask();
        subTask.setTask_id(task);
        subTask.setName(subTaskName);
        subTask.setCompleted(false);
        subTaskRepository.save(subTask);

        return "redirect:/tasks/edit/" + taskId;
    }

    // Для редактирования статуса подзадач (в разработке)
    @PostMapping("/subtasks/{subTaskId}/toggle")
    public String toggleSubTask(@PathVariable Long subTaskId,
                                @RequestParam("taskName") String taskName,
                                @RequestParam("taskDescription") String taskDescription,
                                @RequestParam("dt_beg") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dtBeg,
                                @RequestParam("dt_end") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dtEnd,
                                @RequestParam("statusId") Long statusId ) {

        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("Подзадача не найдена"));

        Task task = subTask.getTask_id();
        task.setName(taskName);
        task.setDescription(taskDescription);
        task.setDt_beg(dtBeg);
        task.setDt_end(dtEnd);

        // Переключение статуса
        boolean newStatus = !Boolean.TRUE.equals(subTask.getCompleted());
        subTask.setCompleted(newStatus);
        subTaskRepository.save(subTask);

        // Если пользовтель убирает чекбокс - скидываем задачу в статус "В ПРОГРЕССЕ"
         if (!newStatus && "ЗАВЕРШЕНА".equals(task.getStatus_id().getName())) {
            TaskStatus inProgress = taskStatusRepository.findByName("В ПРОГРЕССЕ")
                    .orElseThrow(() -> new RuntimeException("Статус В ПРОГРЕССЕ не найден"));

            task.setStatus_id(inProgress);
            System.out.println(newStatus);
        }
        else {
            task.setStatus_id(taskStatusRepository.findById(statusId).orElseThrow(() -> new RuntimeException("Статус не найден")));
        }
        taskRepository.save(task);

        return "redirect:/tasks/edit/" + task.getId();
    }

    // Для удаления подзадач
    @PostMapping("/subtasks/{subTaskId}/delete")
    public String deleteSubTask(@PathVariable Long subTaskId,
                                @RequestParam("taskName") String taskName,
                                @RequestParam("taskDescription") String taskDescription,
                                @RequestParam("dt_beg") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dtBeg,
                                @RequestParam("dt_end") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dtEnd,
                                @RequestParam("statusId") Long statusId){
        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("Подзадача не найдена"));

        Task task = subTask.getTask_id();

        task.setName(taskName);
        task.setDescription(taskDescription);
        task.setDt_beg(dtBeg);
        task.setDt_end(dtEnd);
        task.setStatus_id(taskStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Статус не найден")));
        taskRepository.save(task);

        subTaskRepository.delete(subTask);

        return "redirect:/tasks/edit/" + task.getId();
    }

}
