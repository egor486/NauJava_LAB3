package ru.Dovgan_Egor.NauJava.TEST;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.NauJavaApplication;
import ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK.UserService;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerRestTest {

/*    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }*/

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
        User user = new User();
        user.setName("TestUser");
        user.setLogin("testLogin");
        user.setPassword("testPass");
        user.setRole("USER");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users/add")
                .then()
                .statusCode(200)
                .body("login", equalTo("testLogin"))
                .body("role", equalTo("USER"));
    }

    @Test
    void testAddUser_missingLogin(){
        User user = new User();
        user.setName("TestUserMissing");
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
        User user = new User();
        user.setName("TestUser");
        user.setLogin("testLogin");
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
        User user = new User();
        user.setName("FindMe");
        user.setLogin("findLogin");
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
                .get("/users/findByName/FindMe")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].login", equalTo("findLogin"));
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
