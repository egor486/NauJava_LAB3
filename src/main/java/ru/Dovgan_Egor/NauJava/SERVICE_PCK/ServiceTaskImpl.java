package ru.Dovgan_Egor.NauJava.SERVICE_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.Dovgan_Egor.NauJava.ACCESS_DATA_LAYER_PCK.LayerAccess;
import ru.Dovgan_Egor.NauJava.TASK_PCK.Task;

@Service
public class ServiceTaskImpl implements TaskService{
    private final LayerAccess taskRepos;

    @Autowired
    public ServiceTaskImpl(LayerAccess layerAccess){
        this.taskRepos = layerAccess;
    }

    @Override
    public void createTask(Long id, String name, String description, Boolean status){
        Task newTask = new Task();
        newTask.setId(id);
        newTask.setName(name);
        newTask.setDescription(description);
        newTask.setStatus(status);
        taskRepos.create(newTask);
    }

    @Override
    public Task findById(Long id){
        return taskRepos.read(id);
    }

    @Override
    public void deleteTask(Long id){
        taskRepos.delete(id);
    }

    @Override
    public void updateStatus(Long id, Boolean newStatus){
        Task task = new Task();
        task.setId(id);
        task.setStatus(newStatus);
        taskRepos.update(task);
    }
}
