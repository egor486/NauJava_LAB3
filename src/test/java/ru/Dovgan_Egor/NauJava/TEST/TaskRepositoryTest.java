package ru.Dovgan_Egor.NauJava.TEST;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.SubTaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.SubTask;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.SERVICE.TasksService;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TasksService tasksService;

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;

    @Autowired
    TaskRepositoryTest(TaskRepository taskRepository, SubTaskRepository subTaskRepository) {
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
    }

    @Test
    void testFindSubTasksByTaskId() {

        Task task = new Task();
        task.setName("Main Task " + UUID.randomUUID());
        taskRepository.save(task);


        SubTask subTask = new SubTask();
        subTask.setTask_id(task);
        subTask.setName("Subtask for " + task.getName());
        subTaskRepository.save(subTask);


        List<SubTask> found = subTaskRepository.findByTaskId(task.getId());


        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals(subTask.getId(), found.get(0).getId());
    }
}
