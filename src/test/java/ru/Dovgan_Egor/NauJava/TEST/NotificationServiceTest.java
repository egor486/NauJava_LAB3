package ru.Dovgan_Egor.NauJava.TEST;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.NotificationRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Notification;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.TaskStatus;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;
import ru.Dovgan_Egor.NauJava.SERVICE.NotificationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для NotificationService с использованием Mockito.
 * Покрытие метода checkTaskDeadlines() для проверки просроченных задач.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private TaskStatus newStatus;
    private TaskStatus completedStatus;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setName("Test User");

        newStatus = new TaskStatus();
        newStatus.setId(1L);
        newStatus.setName("НОВАЯ");

        completedStatus = new TaskStatus();
        completedStatus.setId(3L);
        completedStatus.setName("ЗАВЕРШЕНА");
    }

    @Test
    void testCheckTaskDeadlines_createsNotification() {
        // Создаем просроченную задачу
        Task overdueTask = new Task();
        overdueTask.setId(1L);
        overdueTask.setName("Просроченная задача");
        overdueTask.setUser_id(testUser);
        overdueTask.setStatus_id(newStatus);
        
        // Устанавливаем дату окончания в прошлом
        long pastTime = System.currentTimeMillis() - 86400000; // 1 день назад
        overdueTask.setDt_end(new Date(pastTime));

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(overdueTask));
        when(notificationRepository.existsByUserStatusAndMessageMarker(
                eq(testUser), eq(newStatus), anyString())).thenReturn(false);

        // Вызываем метод
        notificationService.checkTaskDeadlines();

        // Проверяем, что уведомление создано
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(notificationRepository).save(argThat(notification ->
                notification.getUser_id().equals(testUser) &&
                notification.getStatus_id().equals(newStatus) &&
                notification.getMessage().contains("просрочена") &&
                !notification.getIs_read()
        ));
    }

    @Test
    void testCheckTaskDeadlines_skipsCompleted() {
        // Создаем просроченную задачу со статусом "ЗАВЕРШЕНА"
        Task completedTask = new Task();
        completedTask.setId(2L);
        completedTask.setName("Завершенная задача");
        completedTask.setUser_id(testUser);
        completedTask.setStatus_id(completedStatus);
        
        long pastTime = System.currentTimeMillis() - 86400000;
        completedTask.setDt_end(new Date(pastTime));

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(completedTask));

        // Вызываем метод
        notificationService.checkTaskDeadlines();

        // Проверяем, что уведомление НЕ создано
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testCheckTaskDeadlines_skipsExisting() {
        // Создаем просроченную задачу
        Task overdueTask = new Task();
        overdueTask.setId(3L);
        overdueTask.setName("Задача с существующим уведомлением");
        overdueTask.setUser_id(testUser);
        overdueTask.setStatus_id(newStatus);
        
        long pastTime = System.currentTimeMillis() - 86400000;
        overdueTask.setDt_end(new Date(pastTime));

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(overdueTask));
        // Уведомление уже существует
        when(notificationRepository.existsByUserStatusAndMessageMarker(
                eq(testUser), eq(newStatus), anyString())).thenReturn(true);

        // Вызываем метод
        notificationService.checkTaskDeadlines();

        // Проверяем, что уведомление НЕ создано повторно
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testCheckTaskDeadlines_noDeadline() {
        // Создаем задачу без дедлайна
        Task taskWithoutDeadline = new Task();
        taskWithoutDeadline.setId(4L);
        taskWithoutDeadline.setName("Задача без дедлайна");
        taskWithoutDeadline.setUser_id(testUser);
        taskWithoutDeadline.setStatus_id(newStatus);
        taskWithoutDeadline.setDt_end(null);

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(taskWithoutDeadline));

        // Вызываем метод
        notificationService.checkTaskDeadlines();

        // Проверяем, что уведомление НЕ создано
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testCheckTaskDeadlines_notOverdue() {
        // Создаем задачу с дедлайном в будущем
        Task futureTask = new Task();
        futureTask.setId(5L);
        futureTask.setName("Будущая задача");
        futureTask.setUser_id(testUser);
        futureTask.setStatus_id(newStatus);
        
        long futureTime = System.currentTimeMillis() + 86400000; // 1 день в будущем
        futureTask.setDt_end(new Date(futureTime));

        when(taskRepository.findAll()).thenReturn(Collections.singletonList(futureTask));

        // Вызываем метод
        notificationService.checkTaskDeadlines();

        // Проверяем, что уведомление НЕ создано
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void testCheckTaskDeadlines_multipleTasks() {
        // Создаем несколько задач с разными состояниями
        Task overdueTask1 = new Task();
        overdueTask1.setId(6L);
        overdueTask1.setName("Просроченная 1");
        overdueTask1.setUser_id(testUser);
        overdueTask1.setStatus_id(newStatus);
        overdueTask1.setDt_end(new Date(System.currentTimeMillis() - 86400000));

        Task overdueTask2 = new Task();
        overdueTask2.setId(7L);
        overdueTask2.setName("Просроченная 2");
        overdueTask2.setUser_id(testUser);
        overdueTask2.setStatus_id(newStatus);
        overdueTask2.setDt_end(new Date(System.currentTimeMillis() - 172800000)); // 2 дня назад

        Task completedTask = new Task();
        completedTask.setId(8L);
        completedTask.setName("Завершенная");
        completedTask.setUser_id(testUser);
        completedTask.setStatus_id(completedStatus);
        completedTask.setDt_end(new Date(System.currentTimeMillis() - 86400000));

        List<Task> tasks = Arrays.asList(overdueTask1, overdueTask2, completedTask);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(notificationRepository.existsByUserStatusAndMessageMarker(
                any(User.class), any(TaskStatus.class), anyString())).thenReturn(false);

        // Вызываем метод
        notificationService.checkTaskDeadlines();

        // Проверяем, что созданы только 2 уведомления (для просроченных незавершенных задач)
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }
}

