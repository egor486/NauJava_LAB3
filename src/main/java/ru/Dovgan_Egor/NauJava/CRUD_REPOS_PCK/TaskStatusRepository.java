package ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK;

import org.springframework.data.repository.CrudRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.TaskStatus;

import java.util.List;

public interface TaskStatusRepository extends CrudRepository <TaskStatus, Long> {

}
