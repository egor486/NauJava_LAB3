package ru.Dovgan_Egor.NauJava.TEST;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.NotificationRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskStatusRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Notification;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.SERVICE.UserService;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тесты для системы уведомлений с использованием Selenium.
 * Покрытие просмотра, отметки и управления уведомлениями.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class NotificationUITest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    private User testUser;
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
        testUserName = "NotifTestUser_" + timestamp;
        testUserLogin = "notiftester_" + timestamp;
        testUserPassword = "test1234";

        // Инициализация статусов задач
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new TaskStatus(1L, "НОВАЯ"));
            taskStatusRepository.save(new TaskStatus(2L, "В ПРОГРЕССЕ"));
            taskStatusRepository.save(new TaskStatus(3L, "ЗАВЕРШЕНА"));
        }

        // Создание тестового пользователя для этого теста
        testUser = new User();
        testUser.setLogin(testUserLogin);
        testUser.setPassword(testUserPassword);
        testUser.setName(testUserName);
        testUser.setRole("USER");
        testUser = userService.addUser(testUser);

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;

        // Логин перед каждым тестом
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
    void testMarkAsRead_success() {
        // Создаем непрочитанное уведомление
        TaskStatus status = taskStatusRepository.findById(1L).orElseThrow();
        
        Notification notification = new Notification();
        notification.setUser_id(testUser);
        notification.setStatus_id(status);
        notification.setMessage("Уведомление для отметки");
        notification.setScheduled_at(new Timestamp(System.currentTimeMillis()));
        notification.setIsRead(false);
        notification = notificationRepository.save(notification);

        // Переходим на страницу задач (где есть непрочитанные уведомления)
        driver.get(baseUrl + "/tasks-page");

        // Ищем кнопку отметки как прочитанное
        List<WebElement> markReadButtons = driver.findElements(
                By.cssSelector("form[action*='/notifications/read/'] button"));
        
        if (!markReadButtons.isEmpty()) {
            markReadButtons.get(0).click();

            // Проверяем, что остались на той же странице или перешли
            wait.until(ExpectedConditions.urlMatches(".*(/tasks-page|/notifications).*"));
            
            // Проверяем в БД, что уведомление прочитано
            Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();
            assertTrue(updated.getIs_read(), "Уведомление должно быть отмечено как прочитанное");
        }
    }

    @Test
    void testViewAllNotifications_page() {
        // Создаем несколько уведомлений (прочитанные и непрочитанные)
        TaskStatus status = taskStatusRepository.findById(1L).orElseThrow();
        
        Notification notification1 = new Notification();
        notification1.setUser_id(testUser);
        notification1.setStatus_id(status);
        notification1.setMessage("Уведомление 1");
        notification1.setScheduled_at(new Timestamp(System.currentTimeMillis()));
        notification1.setIsRead(false);
        notificationRepository.save(notification1);

        Notification notification2 = new Notification();
        notification2.setUser_id(testUser);
        notification2.setStatus_id(status);
        notification2.setMessage("Уведомление 2");
        notification2.setScheduled_at(new Timestamp(System.currentTimeMillis()));
        notification2.setIsRead(true);
        notificationRepository.save(notification2);

        // Переходим на страницу всех уведомлений
        driver.get(baseUrl + "/notifications-page");

        // Проверяем заголовок
        WebElement header = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h2[contains(text(), 'Все уведомления')]")));
        assertTrue(header.isDisplayed(), "Должен быть заголовок 'Все уведомления'");

        // Проверяем, что отображаются оба уведомления
        List<WebElement> notificationCards = driver.findElements(By.cssSelector(".notif-card"));
        assertTrue(notificationCards.size() >= 2, "Должно быть минимум 2 уведомления");
    }

    @Test
    void testNotificationCleaning_removesMarker() {
        // Создаем уведомление с маркером задачи
        TaskStatus status = taskStatusRepository.findById(1L).orElseThrow();
        
        String taskMarker = "[task=123]";
        Notification notification = new Notification();
        notification.setUser_id(testUser);
        notification.setStatus_id(status);
        notification.setMessage("Задача просрочена! " + taskMarker);
        notification.setScheduled_at(new Timestamp(System.currentTimeMillis()));
        notification.setIsRead(false);
        notificationRepository.save(notification);

        // Переходим на страницу всех уведомлений
        driver.get(baseUrl + "/notifications-page");

        // Проверяем, что маркер удален из отображаемого сообщения
        List<WebElement> messages = driver.findElements(By.cssSelector(".notif-header"));
        
        boolean foundWithoutMarker = messages.stream()
                .anyMatch(el -> el.getText().contains("Задача просрочена!") && !el.getText().contains("[task="));
        
        assertTrue(foundWithoutMarker, "Маркер [task=123] должен быть удален из отображаемого сообщения");
    }

    @Test
    void testViewNotifications_emptyState() {
        // Переходим на страницу непрочитанных уведомлений (у нового пользователя их нет)
        driver.get(baseUrl + "/notifications");

        // Можем проверить, что нет ошибок и страница загрузилась
        assertTrue(driver.getCurrentUrl().contains("/notifications"), "Должны быть на странице уведомлений");
    }

    @Test
    void testNotificationNavigation_fromTasksPage() {
        // Создаем уведомление
        TaskStatus status = taskStatusRepository.findById(1L).orElseThrow();
        
        Notification notification = new Notification();
        notification.setUser_id(testUser);
        notification.setStatus_id(status);
        notification.setMessage("Навигационный тест");
        notification.setScheduled_at(new Timestamp(System.currentTimeMillis()));
        notification.setIsRead(false);
        notificationRepository.save(notification);

        // Переходим на страницу задач
        driver.get(baseUrl + "/tasks-page");

        // Кликаем на иконку уведомлений
        WebElement notificationIcon = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[href='/notifications-page']")));
        notificationIcon.click();

        // Проверяем переход на страницу уведомлений
        wait.until(ExpectedConditions.urlContains("/notifications-page"));
        assertTrue(driver.getCurrentUrl().contains("/notifications-page"), 
                "Должен быть переход на страницу уведомлений");
    }
}

