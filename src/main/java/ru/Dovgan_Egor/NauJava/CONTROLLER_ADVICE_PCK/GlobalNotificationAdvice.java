package ru.Dovgan_Egor.NauJava.CONTROLLER_ADVICE_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.NotificationRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Notification;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;

import java.security.Principal;
import java.util.List;

@ControllerAdvice
public class GlobalNotificationAdvice {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Получаем пользователя и его уведомления
    @ModelAttribute
    public void addNotifications(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByLogin(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Notification> unread = notificationRepository.findByUser_idAndIsReadFalse(user);

            // Формируем отдельное сообщение для вывода пользователю
            List<String> messagesForView = unread.stream()
                    .map(n -> n.getMessage().replaceAll("\\[task=\\d+\\]", ""))
                    .toList();

            model.addAttribute("notifications", unread);
            model.addAttribute("notificationMessages", messagesForView);
        }
    }
}