package ru.Dovgan_Egor.NauJava.ENTITY_PCK;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table (name = "task")

public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")

    private User userId;

    @ManyToOne
    private TaskStatus status_id;

    @Column
    private String name;

    @Column
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dt_beg")
    private Date dt_beg;
    //private LocalDate dtBeg;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dt_end")
    private Date dt_end;
    //private LocalDate dtEnd;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTask> subTasks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser_id() {
        return userId;
    }

    public void setUser_id(User user_id) {
        this.userId = user_id;
    }

    public TaskStatus getStatus_id() {
        return status_id;
    }

    public void setStatus_id(TaskStatus status_id) {
        this.status_id = status_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDt_beg() {
        return dt_beg;
    }

    public void setDt_beg(Date dt_beg) {
        this.dt_beg = dt_beg;
    }

    public Date getDt_end() {
        return dt_end;
    }

    public void setDt_end(Date dt_end) {
        this.dt_end = dt_end;
    }
}
