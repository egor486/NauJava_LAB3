package ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK;

import org.springframework.data.repository.CrudRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.SubTask;

import java.util.List;

public interface SubTaskRepository extends CrudRepository <SubTask, Long>{
    List<SubTask> findByTaskId(Long taskId);
}
