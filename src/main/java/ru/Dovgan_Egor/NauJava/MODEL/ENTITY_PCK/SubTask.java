package ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "sub_task")
public class SubTask {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonIgnoreProperties({"subTasks", "user_id", "status_id"})
    private Task task;

    @Column
    private String name;

    @Column(name = "is_completed")
    private Boolean completed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask_id() {
        return task;
    }

    public void setTask_id(Task task) {
        this.task = task;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }


}
