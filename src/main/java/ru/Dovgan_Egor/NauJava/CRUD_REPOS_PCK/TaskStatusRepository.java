package ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskStatusRepository extends CrudRepository <TaskStatus, Long> {
    Optional<TaskStatus> findByName(String name);

    @Query("SELECT ts FROM TaskStatus ts WHERE ts.name <> 'НОВАЯ'")
    List<TaskStatus> findStatusEditUser();
}
