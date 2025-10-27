package ru.Dovgan_Egor.NauJava.DTO_PCK;

import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;

import java.time.LocalDate;

public class TaskDTO {

    private Long id;
    private String name;
    private String description;
    private String statusName;
    private String userLogin;
    private LocalDate dtBeg;
    private LocalDate dtEnd;

    public TaskDTO() {}

    public TaskDTO(Long id, String name, String description, String statusName,
                   String userLogin, LocalDate dtBeg, LocalDate dtEnd) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.statusName = statusName;
        this.userLogin = userLogin;
        this.dtBeg = dtBeg;
        this.dtEnd = dtEnd;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public String getUserLogin() { return userLogin; }
    public void setUserLogin(String userLogin) { this.userLogin = userLogin; }

    public LocalDate getDtBeg() { return dtBeg; }
    public void setDtBeg(LocalDate dtBeg) { this.dtBeg = dtBeg; }

    public LocalDate getDtEnd() { return dtEnd; }
    public void setDtEnd(LocalDate dtEnd) { this.dtEnd = dtEnd; }

    public static TaskDTO fromEntity(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus_id() != null ? task.getStatus_id().getName() : "Нет статуса",
                task.getUser_id() != null ? task.getUser_id().getLogin() : "Нет пользователя",
                task.getDt_beg() != null ? task.getDt_beg().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null,
                task.getDt_end() != null ? task.getDt_end().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null
                //task.getDtBeg(),
                //task.getDtEnd()
        );
    }
}