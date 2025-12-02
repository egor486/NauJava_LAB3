package ru.Dovgan_Egor.NauJava.MODEL.REPOSITORY_PCK;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;

import java.util.List;

@RepositoryRestResource(path = "tasks") // делает REST-эндпоинт по адресу /tasks
public interface TaskRestRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.userId.login = :login")
    List<Task> findTasksByUserLogin(@Param("login") String login);
}