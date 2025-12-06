package ru.Dovgan_Egor.NauJava.TEST;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.ReportRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Report;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.ReportStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST API тесты для ReportController.
 * Покрытие создания и получения асинхронных отчетов.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class ReportControllerRestTest {

    @LocalServerPort
    int port;

    @Autowired
    private ReportRepository reportRepository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void testCreateReport_success() {
        given()
                .when()
                .post("/reports/create")
                .then()
                .statusCode(200)
                .body(containsString("Запустился отчет. ID ="));
    }

    @Test
    void testGetReport_created() {
        // Создаем отчет в статусе CREATED
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        report.setContent("Отчет в процессе...");
        report = reportRepository.save(report);

        given()
                .when()
                .get("/reports/" + report.getId())
                .then()
                .statusCode(200)
                .body(equalTo("Отчет все еще создается"));
    }

    @Test
    void testGetReport_completed() throws InterruptedException {
        // Создаем отчет в статусе COMPLETED
        Report report = new Report();
        report.setStatus(ReportStatus.COMPLETED);
        report.setContent("<h1>Готовый отчет</h1><p>Данные отчета</p>");
        report = reportRepository.save(report);

        given()
                .when()
                .get("/reports/" + report.getId())
                .then()
                .statusCode(200)
                .body(containsString("<h1>Готовый отчет</h1>"));
    }

    @Test
    void testGetReport_notFound() {
        Long nonExistentId = 999999L;

        given()
                .when()
                .get("/reports/" + nonExistentId)
                .then()
                .statusCode(200)
                .body(equalTo("Отчет не найден"));
    }

    @Test
    void testGetReport_error() {
        // Создаем отчет в статусе ERROR
        Report report = new Report();
        report.setStatus(ReportStatus.ERROR);
        report.setContent("Database connection failed");
        report = reportRepository.save(report);

        given()
                .when()
                .get("/reports/" + report.getId())
                .then()
                .statusCode(200)
                .body(containsString("Ошибка генерации: Database connection failed"));
    }

    @Test
    void testCreateAndWaitForReport_integration() throws InterruptedException {
        // Интеграционный тест: создание и ожидание завершения отчета
        String response = given()
                .when()
                .post("/reports/create")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        // Извлекаем ID из ответа
        Long reportId = Long.parseLong(response.replaceAll(".*ID = (\\d+)", "$1"));

        // Ждем некоторое время для асинхронной генерации
        Thread.sleep(1000);

        // Проверяем, что отчет создан или в процессе
        given()
                .when()
                .get("/reports/" + reportId)
                .then()
                .statusCode(200)
                .body(anyOf(
                        containsString("Отчет все еще создается"),
                        containsString("<h1>Отчёт статистики</h1>")
                ));
    }
}

