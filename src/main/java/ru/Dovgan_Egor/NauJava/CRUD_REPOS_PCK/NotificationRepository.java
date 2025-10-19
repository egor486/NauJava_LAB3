package ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK;

import org.springframework.data.repository.CrudRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Notification;

import java.util.List;

public interface NotificationRepository extends CrudRepository <Notification, Long> {
    List<Notification> findByIsReadFalse();
}
