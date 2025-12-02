package ru.Dovgan_Egor.NauJava.TEST;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK.UserService;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        /*userService = new UserService();
        userService.userRepository = userRepository;
        userService.passwordEncoder = passwordEncoder;*/
        userService = new UserService(userRepository, passwordEncoder);
    }

    // Тесты

    // На добавление нового пользователя

    /* Позитивный */
    @Test
    void addUser_positive() {

        System.out.println("Стартовал позитивный тест на добавление пользователя");
        System.out.println("================================");

        User user = new User();
        user.setPassword("123");

        when(passwordEncoder.encode("123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.addUser(user);

        assertEquals("encodedPass", saved.getPassword());
        assertNotNull(saved.getCreated_at());
        verify(userRepository, times(1)).save(user);
    }

    /* Негативный */
    @Test
    void addUser_negative_nullUser() {

        System.out.println("Стартовал негативный тест на добавление пользователя");
        System.out.println("================================");

        assertThrows(NullPointerException.class, () -> userService.addUser(null));
    }

    // На получение пользователя по имени

    /* Позитивный */
    @Test
    void getUserByName_positive() {

        System.out.println("Стартовал позитивный тест на получение по имени пользователя");
        System.out.println("================================");

        when(userRepository.findByName("Egor"))
                .thenReturn(List.of(new User()));

        List<User> result = userService.getUserByName("Egor");

        assertEquals(1, result.size());

    }

    /* Негативный */
    @Test
    void getUserByName_negative_notFound() {

        System.out.println("Стартовал негативный тест на получение по имени пользователя");
        System.out.println("================================");

        when(userRepository.findByName("Unknown")).thenReturn(List.of());

        List<User> result = userService.getUserByName("Unknown");

        assertTrue(result.isEmpty());

    }


    // На получение пользователя по ID

    /* Позитивный */
    @Test
    void getUserById_positive() {

        System.out.println("Стартовал позитивный тест на получение по id пользователя");
        System.out.println("================================");

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());

    }

    /* Негативный */
    @Test
    void testGetUserById_NotFound() {

        System.out.println("Стартовал негативный тест на получение по id пользователя");
        System.out.println("================================");


        when(userRepository.findById(99L)).thenReturn(Optional.empty());


        Exception ex = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(99L).orElseThrow(
                    () -> new RuntimeException("User not found: 99")
            );
        });

        // Выводим текст исключения в консоль
        System.out.println("Выбрасываем исключение");
        System.out.println(ex.getMessage());
        System.out.println("================================");

        // Проверяем текст исключения
        assertEquals("User not found: 99", ex.getMessage());
    }


    // Получение всех пользователей
    @Test
    void getAllUsers_positive() {
        when(userRepository.findAll())
                .thenReturn(List.of(new User(), new User()));

        Iterable<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, ((List<?>) users).size());
    }
}

