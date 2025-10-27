package ru.Dovgan_Egor.NauJava.REPOSITORY_PCK;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;

@RepositoryRestResource(path = "tasks") // ← делает REST-эндпоинт по адресу /tasks
public interface TaskRestRepository extends JpaRepository<Task, Long> {
}