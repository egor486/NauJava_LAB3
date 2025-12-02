package ru.Dovgan_Egor.NauJava.SERVICE_PCK;

import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;

public interface TaskService {
    void createTask(Long id, String name, String description, Boolean status);

    Task findById(Long id);

    void deleteTask(Long id);

    void updateStatus(Long id, Boolean newStatus);
}


