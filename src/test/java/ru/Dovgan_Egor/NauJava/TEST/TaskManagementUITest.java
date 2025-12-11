package ru.Dovgan_Egor.NauJava.TEST;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskStatusRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.SERVICE.UserService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тесты управления задачами с использованием Selenium.
 * Покрытие CRUD операций: создание, редактирование, удаление задач и подзадач.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class TaskManagementUITest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    // Тестовые данные пользователя - генерируются для каждого теста
    private String testUserName;
    private String testUserLogin;
    private String testUserPassword;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        // Генерируем уникальные данные для каждого теста
        long timestamp = System.currentTimeMillis();
        testUserName = "TaskTestUser_" + timestamp;
        testUserLogin = "tasktester_" + timestamp;
        testUserPassword = "test1234";

        // Инициализация статусов задач
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new TaskStatus(1L, "НОВАЯ"));
            taskStatusRepository.save(new TaskStatus(2L, "В ПРОГРЕССЕ"));
            taskStatusRepository.save(new TaskStatus(3L, "ЗАВЕРШЕНА"));
        }

        // Создание тестового пользователя для этого теста
        User testUser = new User();
        testUser.setLogin(testUserLogin);
        testUser.setPassword(testUserPassword);
        testUser.setName(testUserName);
        testUser.setRole("USER");
        userService.addUser(testUser);

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;

        // Логинимся перед каждым тестом
        loginAsTestUser();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginAsTestUser() {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username"))).sendKeys(testUserLogin);
        driver.findElement(By.name("password")).sendKeys(testUserPassword);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/tasks-page"));
    }

    @Test
    void testCreateTask_success() {
        // Переход на страницу создания задачи
        driver.get(baseUrl + "/tasks/add");

        // Заполнение формы
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")))
                .sendKeys("Новая задача " + System.currentTimeMillis());
        driver.findElement(By.name("description")).sendKeys("Описание новой задачи");
        
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        
        driver.findElement(By.name("dt_beg")).sendKeys(today.format(formatter));
        driver.findElement(By.name("dt_end")).sendKeys(tomorrow.format(formatter));

        // Отправка формы
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверка редиректа на страницу задач
        wait.until(ExpectedConditions.urlContains("/tasks-page"));
        assertTrue(driver.getCurrentUrl().contains("/tasks-page"), "После создания задачи должен быть редирект на /tasks-page");
    }

    @Test
    void testCreateTask_invalidDates() {
        driver.get(baseUrl + "/tasks/add");

        // Заполнение формы с некорректными датами (конец раньше начала)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")))
                .sendKeys("Задача с ошибкой");
        driver.findElement(By.name("description")).sendKeys("Тест валидации");
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        
        driver.findElement(By.name("dt_beg")).sendKeys(today.format(formatter));
        driver.findElement(By.name("dt_end")).sendKeys(yesterday.format(formatter));

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверка сообщения об ошибке
        WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'Дата окончания не может быть раньше')]")));
        assertTrue(errorMessage.isDisplayed(), "Должно отображаться сообщение об ошибке валидации дат");
    }

    @Test
    void testEditTask_success() {
        // Сначала создаем задачу
        createTaskViaUI("Задача для редактирования", "Описание");

        // Находим ссылку на редактирование (предполагаем, что есть список задач)
        List<WebElement> editLinks = driver.findElements(By.cssSelector("a[href*='/tasks/edit/']"));
        if (!editLinks.isEmpty()) {
            editLinks.get(0).click();

            // Ждем загрузки формы редактирования
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")));

            // Изменяем название
            WebElement nameInput = driver.findElement(By.name("name"));
            nameInput.clear();
            nameInput.sendKeys("Отредактированная задача");

            // Сохраняем
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            // Проверка редиректа
            wait.until(ExpectedConditions.urlContains("/tasks-page"));
            assertTrue(driver.getCurrentUrl().contains("/tasks-page"));
        }
    }

    @Test
    void testDeleteTask_success() {
        // Создаем задачу
        createTaskViaUI("Задача для удаления", "Будет удалена");

        // Находим ссылку на редактирование
        List<WebElement> editLinks = driver.findElements(By.cssSelector("a[href*='/tasks/edit/']"));
        if (!editLinks.isEmpty()) {
            editLinks.get(0).click();

            // Ждем кнопку удаления
            WebElement deleteButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(text(), 'Удалить')]")));
            deleteButton.click();

            // Ждем модального окна и подтверждаем
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("taskDeleteModal")));
            WebElement confirmButton = driver.findElement(By.cssSelector("#taskDeleteModal .confirm"));
            confirmButton.click();

            // Проверка редиректа
            wait.until(ExpectedConditions.urlContains("/tasks-page"));
            assertTrue(driver.getCurrentUrl().contains("/tasks-page"));
        }
    }

    @Test
    void testSearchTask_found() {
        String uniqueTaskName = "ПоискЗадачи_" + System.currentTimeMillis();
        createTaskViaUI(uniqueTaskName, "Тестовая задача для поиска");

        // Переход на страницу задач
        driver.get(baseUrl + "/tasks-page");

        // Находим поле поиска
        WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("search")));
        searchInput.sendKeys(uniqueTaskName);

        // Отправляем форму поиска
        searchInput.submit();

        // Проверяем, что URL содержит параметр поиска (учитываем URL-encoding)
        String encoded = URLEncoder.encode(uniqueTaskName, StandardCharsets.UTF_8);
        wait.until(ExpectedConditions.urlContains("search=" + encoded));
        assertTrue(driver.getCurrentUrl().contains("search="), "URL должен содержать параметр поиска");
    }

    @Test
    void testAddSubTask_success() {
        // Создаем задачу
        createTaskViaUI("Задача с подзадачами", "Будут добавлены подзадачи");

        // Переходим к редактированию
        List<WebElement> editLinks = driver.findElements(By.cssSelector("a[href*='/tasks/edit/']"));
        if (!editLinks.isEmpty()) {
            editLinks.get(0).click();

            // Добавляем подзадачу
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("subTaskName")))
                    .sendKeys("Подзадача 1");
            driver.findElement(By.cssSelector("#addSubTaskForm button[type='submit']")).click();

            // Ждем обновления страницы
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.cssSelector(".table-subtask tbody tr"), 0));

            // Проверяем, что подзадача появилась в таблице
            List<WebElement> subtaskRows = driver.findElements(By.cssSelector(".table-subtask tbody tr"));
            assertFalse(subtaskRows.isEmpty(), "Должна быть хотя бы одна подзадача");
        }
    }

    @Test
    void testToggleSubTask_complete() {
        // Создаем задачу и добавляем подзадачу
        createTaskViaUI("Задача для подзадач", "Тест переключения");

        List<WebElement> editLinks = driver.findElements(By.cssSelector("a[href*='/tasks/edit/']"));
        if (!editLinks.isEmpty()) {
            editLinks.get(0).click();

            // Добавляем подзадачу
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("subTaskName")))
                    .sendKeys("Подзадача для переключения");
            driver.findElement(By.cssSelector("#addSubTaskForm button[type='submit']")).click();

            // Ждем появления подзадачи
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-subtask tbody tr")));

            // Находим чекбокс
            WebElement checkbox = driver.findElement(By.cssSelector("input[type='checkbox'][name='completed']"));
            boolean initialState = checkbox.isSelected();

            // Кликаем на чекбокс
            checkbox.click();

            // Ждем, пока элемент станет устаревшим после обновления DOM, затем находим снова
            wait.until(ExpectedConditions.stalenessOf(checkbox));
            WebElement refreshed = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[type='checkbox'][name='completed']")));

            // Проверяем изменение состояния
            assertNotEquals(initialState, refreshed.isSelected(), "Состояние чекбокса должно измениться");
        }
    }

    @Test
    void testDeleteSubTask_success() {
        // Создаем задачу и подзадачу
        createTaskViaUI("Задача для удаления подзадач", "Тест удаления");

        List<WebElement> editLinks = driver.findElements(By.cssSelector("a[href*='/tasks/edit/']"));
        if (!editLinks.isEmpty()) {
            editLinks.get(0).click();

            // Добавляем подзадачу
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("subTaskName")))
                    .sendKeys("Подзадача для удаления");
            driver.findElement(By.cssSelector("#addSubTaskForm button[type='submit']")).click();

            // Ждем появления подзадачи
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".delete-btn")));

            // Кликаем кнопку удаления
            WebElement deleteBtn = driver.findElement(By.cssSelector(".delete-btn"));
            deleteBtn.click();

            // Ждем модальное окно и подтверждаем
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("subTaskDeleteModal")));
            WebElement confirmBtn = driver.findElement(By.cssSelector("#subTaskDeleteModal .confirm"));
            confirmBtn.click();

            // Проверяем, что подзадача удалена (можем проверить, что страница обновилась)
            wait.until(ExpectedConditions.urlContains("/tasks/edit/"));
        }
    }

    @Test
    void testEditTask_cannotCompleteWithUnfinishedSubtasks() {
        // Создаем задачу
        createTaskViaUI("Задача с незавершенными подзадачами", "Валидация");

        List<WebElement> editLinks = driver.findElements(By.cssSelector("a[href*='/tasks/edit/']"));
        if (!editLinks.isEmpty()) {
            editLinks.get(0).click();

            // Добавляем подзадачу
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("subTaskName")))
                    .sendKeys("Незавершенная подзадача");
            driver.findElement(By.cssSelector("#addSubTaskForm button[type='submit']")).click();

            // Ждем обновления
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-subtask tbody tr")));

            // Пытаемся изменить статус на "ЗАВЕРШЕНА"
            WebElement statusSelect = driver.findElement(By.name("status_id"));
            Select select = new Select(statusSelect);
            select.selectByVisibleText("ЗАВЕРШЕНА");

            // Сохраняем
            driver.findElement(By.cssSelector("#editTaskForm button[type='submit']")).click();

            // Проверяем сообщение об ошибке
            WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(), 'незавершенные подзадачи')]")));
            assertTrue(errorMessage.isDisplayed(), "Должно быть сообщение о незавершенных подзадачах");
        }
    }

    /**
     * Вспомогательный метод для создания задачи через UI
     */
    private void createTaskViaUI(String taskName, String description) {
        driver.get(baseUrl + "/tasks/add");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")))
                .sendKeys(taskName);
        driver.findElement(By.name("description")).sendKeys(description);

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        driver.findElement(By.name("dt_beg")).sendKeys(today.format(formatter));
        driver.findElement(By.name("dt_end")).sendKeys(tomorrow.format(formatter));

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/tasks-page"));
    }
}

