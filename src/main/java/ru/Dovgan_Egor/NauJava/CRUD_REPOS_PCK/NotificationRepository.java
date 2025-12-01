package ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Notification;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.TaskStatus;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.User;

import java.util.List;

public interface NotificationRepository extends CrudRepository <Notification, Long> {
    List<Notification> findByIsReadFalse();

    //List<Notification> findByUser_idAndIsReadFalse(User user);
    @Query("SELECT n FROM Notification n WHERE n.user_id = :user AND n.isRead = false")
    List<Notification> findByUser_idAndIsReadFalse(@Param("user") User user);

    @Query("SELECT n FROM Notification n WHERE n.user_id = :user AND n.status_id = :status")
    List<Notification> findByUserAndStatus(@Param("user") User user, @Param("status") TaskStatus status);

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.user_id = :user AND n.status_id = :status AND n.message LIKE %:marker%")
    boolean existsByUserStatusAndMessageMarker(
            @Param("user") User user,
            @Param("status") TaskStatus status,
            @Param("marker") String marker
    );
}
