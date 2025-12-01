package ru.Dovgan_Egor.NauJava.TEST;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginLogoutUITest {

    private WebDriver driver;

    @BeforeAll
    void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
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
    void testLoginAndLogout() throws InterruptedException {
        String baseUrl = "http://localhost:8080/login"; // URL страницы входа
        driver.get(baseUrl);

        WebElement loginInput = driver.findElement(By.name("username")); // или "login" в зависимости от формы UPD 02.12.2025 теперь всегда username вне зависимости
        WebElement passwordInput = driver.findElement(By.name("password"));

        loginInput.sendKeys("egrior"); // корректный логин
        passwordInput.sendKeys("1234"); // корректный пароль

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        Thread.sleep(1000);
        assertTrue(driver.getCurrentUrl().contains("/tasks-page"));

        WebElement logoutButton = driver.findElement(By.id("logout"));
        assertTrue(logoutButton.isDisplayed());

        logoutButton.click();

        Thread.sleep(500);

        assertEquals("http://localhost:8080/login", driver.getCurrentUrl());
    }
}
