package ru.Dovgan_Egor.NauJava.TEST;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.SERVICE.UserService;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class UserControllerRestTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.core.env.Environment env;

    @Test
    void checkActiveProfile() {
        System.out.println("Active profiles: " + String.join(", ", env.getActiveProfiles()));
    }

    // Тесты на POST addUser

    @Test
    void testAddUser_success(){
        String uniqueLogin = "testLogin_" + System.currentTimeMillis();
        String uniqueName = "TestUser_" + System.currentTimeMillis();
        
        User user = new User();
        user.setName(uniqueName);
        user.setLogin(uniqueLogin);
        user.setPassword("testPass");
        user.setRole("USER");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/add")
                .then()
                .statusCode(200)
                .body("login", equalTo(uniqueLogin))
                .body("role", equalTo("USER"));
    }

    @Test
    void testAddUser_missingLogin(){
        String uniqueName = "TestUserMissing_" + System.currentTimeMillis();
        
        User user = new User();
        user.setName(uniqueName);
        user.setPassword("testPass");
        user.setRole("USER");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/add")
                .then()
                .statusCode(400);
    }


    // Тест на получение всех пользователей

    @Test
    void testGetAllUsers(){
        String uniqueLogin = "testLogin_" + System.currentTimeMillis();
        String uniqueName = "TestUser_" + System.currentTimeMillis();
        
        User user = new User();
        user.setName(uniqueName);
        user.setLogin(uniqueLogin);
        user.setPassword("testPass");
        user.setRole("USER");
        userService.addUser(user);

        given()
                .when()
                .get("/users/all")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    // Тест на получение пользователя по имени

    @Test
    void testFindByName(){
        String uniqueLogin = "findLogin_" + System.currentTimeMillis();
        String uniqueName = "FindMe_" + System.currentTimeMillis();
        
        User user = new User();
        user.setName(uniqueName);
        user.setLogin(uniqueLogin);
        user.setPassword("pass");
        user.setRole("USER");


        // Сначала добавляем пользователя
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/add")
                .then()
                .statusCode(200);

        // Ищем пользователя по имени
        given()
                .accept(ContentType.JSON)
                .get("/users/findByName/" + uniqueName)
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].login", equalTo(uniqueLogin));
    }

    @Test
    void testFindByName_notFound() {
        given()
                .get("/users/findByName/UnknownUser")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }



}
