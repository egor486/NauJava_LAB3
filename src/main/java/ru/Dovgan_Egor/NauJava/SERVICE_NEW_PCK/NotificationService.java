package ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.NotificationRepository;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.TaskRepository;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Notification;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;

import java.sql.Timestamp;
import java.util.List;


@Service
public class NotificationService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // запускаем каждые 30 секунд
    @Scheduled(fixedDelay = 30000)
    public void checkTaskDeadlines() {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        List<Task> tasks = (List<Task>) taskRepository.findAll();

        for (Task task : tasks) {

            if (task.getDt_end() == null) continue;

            if (task.getStatus_id().getId() == 3) {
                continue;
            }

            Timestamp deadline = new Timestamp(task.getDt_end().getTime());

            // если задача просрочена
            if (deadline.before(now)) {

                // Добавим маркер для определения по: задаче, статусу и пользовтелю, до этого была неверная логика(проверялся только статус и пользователь)
                String marker = "[task=" + task.getId() + "]";

/*                boolean exists = !notificationRepository
                        .findByUserAndStatus(task.getUser_id(), task.getStatus_id())
                        .isEmpty();*/
                boolean exists = notificationRepository.existsByUserStatusAndMessageMarker(
                        task.getUser_id(),
                        task.getStatus_id(),
                        marker
                );

                if (!exists) {
                    Notification n = new Notification();
                    n.setUser_id(task.getUser_id());
                    n.setStatus_id(task.getStatus_id());
                    n.setMessage("Задача \"" + task.getName() + "\" просрочена!" + marker);
                    n.setScheduled_at(now);
                    n.setIsRead(false);

                    notificationRepository.save(n);
                }
            }
        }
    }
}