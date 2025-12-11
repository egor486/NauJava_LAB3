package ru.Dovgan_Egor.NauJava.TEST;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskStatusRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.MODEL.REPOSITORY_PCK.TaskRestRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST API тесты для TaskController.
 * Покрытие эндпоинтов поиска задач по датам и пользователю.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class TaskControllerRestTest {

    @LocalServerPort
    int port;

    @Autowired
    private TaskRestRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;

        // Инициализация статусов задач
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new TaskStatus(1L, "НОВАЯ"));
            taskStatusRepository.save(new TaskStatus(2L, "В ПРОГРЕССЕ"));
            taskStatusRepository.save(new TaskStatus(3L, "ЗАВЕРШЕНА"));
        }
    }

    @Test
    void testGetTasksBetweenDates_success() throws Exception {
        // Подготовка данных
        Calendar cal = Calendar.getInstance();
        
        cal.set(2025, Calendar.JANUARY, 5);
        Date startDate = cal.getTime();
        
        cal.set(2025, Calendar.JANUARY, 15);
        Date middleDate = cal.getTime();
        
        cal.set(2025, Calendar.JANUARY, 25);
        Date endDate = cal.getTime();

        Task task1 = new Task();
        task1.setName("Task1_" + System.currentTimeMillis());
        task1.setDt_beg(middleDate);
        taskRepository.save(task1);

        // Используем формат yyyy/MM/dd, который по умолчанию понимает Spring Boot для Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String start = sdf.format(startDate);
        String end = sdf.format(endDate);

        // Выполнение запроса
        given()
                .queryParam("start", start)
                .queryParam("end", end)
                .when()
                .get("/between")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("name", hasItem(task1.getName()));
    }

    @Test
    void testGetTasksBetweenDates_empty() throws Exception {
        // Используем формат yyyy/MM/dd, который по умолчанию понимает Spring Boot для Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        
        Calendar cal = Calendar.getInstance();
        cal.set(2020, Calendar.JANUARY, 1);
        String start = sdf.format(cal.getTime());
        
        cal.set(2020, Calendar.JANUARY, 2);
        String end = sdf.format(cal.getTime());

        given()
                .queryParam("start", start)
                .queryParam("end", end)
                .when()
                .get("/between")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    void testGetTasksBetweenDates_invalidDates() {
        given()
                .queryParam("start", "invalid-date")
                .queryParam("end", "2025-01-01")
                .when()
                .get("/between")
                .then()
                .statusCode(500);
    }

    @Test
    void testGetTasksByUserLogin_success() {
        // Создание пользователя и задачи
        String uniqueLogin = "user_" + System.currentTimeMillis();
        String uniqueName = "User_" + System.currentTimeMillis();
        
        User user = new User();
        user.setLogin(uniqueLogin);
        user.setName(uniqueName);
        userRepository.save(user);

        Task task = new Task();
        task.setName("UserTask_" + System.currentTimeMillis());
        task.setUser_id(user);
        taskRepository.save(task);

        // Выполнение запроса
        given()
                .queryParam("login", uniqueLogin)
                .when()
                .get("/by-user")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("name", hasItem(task.getName()));
    }

    @Test
    void testGetTasksByUserLogin_notFound() {
        String nonExistentLogin = "nonexistent_" + System.currentTimeMillis();

        given()
                .queryParam("login", nonExistentLogin)
                .when()
                .get("/by-user")
                .then()
                .statusCode(200) // Ожидаем пустой список, а не 404
                .body("$", hasSize(0));
    }
}
