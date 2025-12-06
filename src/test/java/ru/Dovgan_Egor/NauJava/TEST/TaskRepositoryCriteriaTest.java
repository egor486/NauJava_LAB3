package ru.Dovgan_Egor.NauJava.TEST;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.DAO_PCK.TaskRepositoryCustom;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class TaskRepositoryCustomTest {

    private final TaskRepository taskRepository;
    private final TaskRepositoryCustom taskRepositoryCustom;
    private final UserRepository userRepository;

    @Autowired
    TaskRepositoryCustomTest(TaskRepository taskRepository, TaskRepositoryCustom taskRepositoryCustom, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskRepositoryCustom = taskRepositoryCustom;
        this.userRepository = userRepository;
    }


    @Test
    void testFindByDtBegBetween() {

        Calendar cal = Calendar.getInstance();

        cal.set(2025, Calendar.JANUARY, 1);
        Date date1 = cal.getTime();
        cal.set(2025, Calendar.JANUARY, 10);
        Date date2 = cal.getTime();

        Task task1 = new Task();
        task1.setName("Task1 " + UUID.randomUUID());
        task1.setDt_beg(date1);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setName("Task2 " + UUID.randomUUID());
        task2.setDt_beg(date2);
        taskRepository.save(task2);


        cal.set(2025, Calendar.JANUARY, 1);
        Date start = cal.getTime();
        cal.set(2025, Calendar.JANUARY, 15);
        Date end = cal.getTime();

        List<Task> found = taskRepositoryCustom.findByDtBegBetween(start, end);

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertTrue(found.stream().anyMatch(t -> t.getId().equals(task1.getId())));
        Assertions.assertTrue(found.stream().anyMatch(t -> t.getId().equals(task2.getId())));
    }


    @Test
    void testFindTasksByUserLogin() {

        // Используем уникальный логин для каждого запуска теста
        String uniqueLogin = "user_" + System.currentTimeMillis();
        String uniqueName = "Test User " + System.currentTimeMillis();

        User user = new User();
        user.setLogin(uniqueLogin);
        user.setName(uniqueName);
        userRepository.save(user);


        Task task = new Task();
        task.setName("UserTask " + UUID.randomUUID());
        task.setUser_id(user);
        taskRepository.save(task);

        List<Task> found = taskRepositoryCustom.findTasksByUserLogin(uniqueLogin);

        Assertions.assertFalse(found.isEmpty());
        Assertions.assertEquals(uniqueLogin, found.get(0).getUser_id().getLogin());
    }
}