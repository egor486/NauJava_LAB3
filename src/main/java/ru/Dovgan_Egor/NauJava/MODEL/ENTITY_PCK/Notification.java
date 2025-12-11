package ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table (name = "notification")
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private TaskStatus status_id;

    @ManyToOne
    private User user_id;

    @Column
    private String message;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column
    private Timestamp scheduled_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskStatus getStatus_id() {
        return status_id;
    }

    public void setStatus_id(TaskStatus status_id) {
        this.status_id = status_id;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIs_read() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Timestamp getScheduled_at() {
        return scheduled_at;
    }

    public void setScheduled_at(Timestamp scheduled_at) {
        this.scheduled_at = scheduled_at;
    }
}
