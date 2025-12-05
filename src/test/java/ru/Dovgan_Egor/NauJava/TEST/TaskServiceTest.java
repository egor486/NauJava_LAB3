package ru.Dovgan_Egor.NauJava.TEST;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.SubTaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.SubTask;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.SERVICE.TasksService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class TasksServiceTest {

    private final TasksService tasksService;
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;

    @Autowired
    TasksServiceTest(TasksService tasksService, TaskRepository taskRepository, SubTaskRepository subTaskRepository) {
        this.tasksService = tasksService;
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
    }

    @Test
    void testDeleteTaskWithSubTasks_Success() {
        Task task = new Task();
        task.setName("Task " + UUID.randomUUID());
        taskRepository.save(task);

        SubTask sub1 = new SubTask();
        sub1.setTask_id(task);
        sub1.setName("Sub1");
        subTaskRepository.save(sub1);

        SubTask sub2 = new SubTask();
        sub2.setTask_id(task);
        sub2.setName("Sub2");
        subTaskRepository.save(sub2);

        tasksService.deleteTaskWithSubTasks(task.getId());

        Optional<Task> foundTask = taskRepository.findById(task.getId());
        Assertions.assertTrue(foundTask.isEmpty());

        List<SubTask> foundSubs = subTaskRepository.findByTaskId(task.getId());
        Assertions.assertTrue(foundSubs.isEmpty());
    }


    @Test
    void testDeleteTaskWithSubTasks_Rollback() {

        Task task = new Task();
        task.setName("RollbackTask " + UUID.randomUUID());
        taskRepository.save(task);

        SubTask sub = new SubTask();
        sub.setTask_id(task);
        sub.setName("Sub for rollback");
        subTaskRepository.save(sub);

        Assertions.assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Ошибка во время транзакции");
        });

        Optional<Task> foundTask = taskRepository.findById(task.getId());
        Assertions.assertTrue(foundTask.isPresent());
    }
}
