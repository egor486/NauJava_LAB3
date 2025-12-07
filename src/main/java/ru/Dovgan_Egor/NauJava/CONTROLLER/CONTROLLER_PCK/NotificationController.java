package ru.Dovgan_Egor.NauJava.CONTROLLER.CONTROLLER_PCK;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.NotificationRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Notification;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;

import java.security.Principal;
import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Получение уведомления
    @GetMapping("/notifications")
    public String viewNotifications(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        //List<Notification> list = notificationRepository.findByIsReadFalse();
        List<Notification> list = notificationRepository.findByUser_idAndIsReadFalse(user);
        model.addAttribute("notifications", list);
        return "notifications";
    }

    // Прочтение уведомления
    @PostMapping("/notifications/read/{id}")
    public String markRead(@PathVariable Long id, HttpServletRequest request, Principal principal) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Не найдено уведомление"));

        if (!n.getUser_id().getLogin().equals(principal.getName())) {
            throw new RuntimeException("Access denied");
        }
        n.setIsRead(true);
        notificationRepository.save(n);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/tasks-page");
    }

    // Визуализация всех уведомлений пользователя
    @GetMapping("/notifications-page")
    public String viewAllNotifications(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Notification> list = notificationRepository.findAllByUserOrderByDate(user);

        list.forEach(n -> {
            if (n.getMessage() != null) {
                String cleaned = n.getMessage()
                        .replaceAll("\\[task=\\d+]", "")
                        .trim();
                n.setMessage(cleaned);
            }
        });

        model.addAttribute("notifications", list);
        return "notifications-page";
    }

}