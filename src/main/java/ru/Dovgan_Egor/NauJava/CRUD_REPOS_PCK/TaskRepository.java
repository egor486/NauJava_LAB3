package ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.Dovgan_Egor.NauJava.DAO_PCK.TaskRepositoryCustom;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByDtBegBetween(Date start, Date end);

    @Query("SELECT t FROM Task t WHERE t.user_id.login = :login")
    List<Task> findTasksByUserLogin(@Param("login") String login);
}
