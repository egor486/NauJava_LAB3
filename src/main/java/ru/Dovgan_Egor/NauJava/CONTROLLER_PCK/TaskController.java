package ru.Dovgan_Egor.NauJava.CONTROLLER_PCK;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.Dovgan_Egor.NauJava.DAO_PCK.TaskRepositoryCustom;
import ru.Dovgan_Egor.NauJava.DTO_PCK.TaskDTO;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class TaskController {
    private final TaskRepositoryCustom taskRepositoryCustom;

    public TaskController(TaskRepositoryCustom taskRepositoryCustom) {
        this.taskRepositoryCustom = taskRepositoryCustom;
    }

    @GetMapping("/between")
    public List<TaskDTO> getTasksBetweenDates(
            @RequestParam("start") Date start,
            @RequestParam("end") Date end
            ) {
        return taskRepositoryCustom.findByDtBegBetween(start, end)
                .stream()
                .map(TaskDTO::fromEntity)
                .toList();
    }

    @GetMapping("/by-user")
    public List<TaskDTO> getTasksByUserLogin(@RequestParam("login") String login) {
        return taskRepositoryCustom.findTasksByUserLogin(login)
                .stream()
                .map(TaskDTO::fromEntity)
                .toList();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Ресурс не найден: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла ошибка: " + ex.getMessage());
    }

}
