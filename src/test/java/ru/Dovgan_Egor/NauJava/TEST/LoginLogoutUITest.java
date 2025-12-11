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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI-тест полного цикла работы пользователя:
 * 1. Регистрация нового пользователя
 * 2. Логин с зарегистрированными данными
 * 3. Логаут из системы
 * 
 * Использует Selenium WebDriver для автоматизации браузера Chrome.
 * Тест запускается на случайном порту с отдельной тестовой БД (FOR_TEST).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Используем application-test.properties для настройки БД
@Import(TestSecurityConfig.class) // Импортируем тестовую Security-конфигурацию
public class LoginLogoutUITest {

    @LocalServerPort
    private int port;

    @Autowired
    private ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskStatusRepository taskStatusRepository;

    private WebDriver driver;

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
        testUserName = "Test User " + timestamp;
        testUserLogin = "testuser" + timestamp;
        testUserPassword = "test1234";

        // Инициализируем статусы задач, если их нет
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus(1L, "НОВАЯ"));
            taskStatusRepository.save(new ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus(2L, "В ПРОГРЕССЕ"));
            taskStatusRepository.save(new ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus(3L, "ЗАВЕРШЕНА"));
        }

        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testRegistrationLoginAndLogout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String baseUrl = "http://localhost:" + port;

        // ========== ЭТАП 1: РЕГИСТРАЦИЯ ==========
        driver.get(baseUrl + "/registration");

        // Ждем загрузки формы регистрации
        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("name")));
        WebElement loginInput = driver.findElement(By.name("login"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement registerButton = driver.findElement(By.cssSelector("input[type='submit']"));

        // Заполняем форму регистрации
        nameInput.sendKeys(testUserName);
        loginInput.sendKeys(testUserLogin);
        passwordInput.sendKeys(testUserPassword);
        registerButton.click();

        // После успешной регистрации должен быть редирект на /login
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/login"));
        assertEquals(baseUrl + "/login", driver.getCurrentUrl(), "После регистрации должен быть редирект на /login");

        // ========== ЭТАП 2: ЛОГИН ==========
        // Ждем загрузки формы логина
        WebElement loginUsernameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        WebElement loginPasswordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        // Вводим учетные данные зарегистрированного пользователя
        loginUsernameInput.sendKeys(testUserLogin);
        loginPasswordInput.sendKeys(testUserPassword);
        loginButton.click();

        // Ждем редиректа на страницу с задачами
        wait.until(ExpectedConditions.urlContains("/tasks-page"));
        assertTrue(driver.getCurrentUrl().contains("/tasks-page"), "После логина должен быть редирект на /tasks-page");

        // ========== ЭТАП 3: ЛОГАУТ ==========
        // Ждем появления кнопки выхода
        WebElement logoutButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logout")));
        assertTrue(logoutButton.isDisplayed(), "Кнопка logout должна быть видна");

        logoutButton.click();

        // Ждем редиректа обратно на страницу логина
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/login"));
        assertEquals(baseUrl + "/login", driver.getCurrentUrl(), "После logout должен быть редирект на /login");
    }
}