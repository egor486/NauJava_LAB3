package ru.Dovgan_Egor.NauJava.ACCESS_DATA_LAYER_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;

import java.util.List;

@Component
public class LayerAccess implements CRUD_REP<Task, Long>{
    private final List<Task> taskContainer;

    @Autowired
    public LayerAccess(List<Task> taskContainer){
        this.taskContainer = taskContainer;
    }

    @Override
    public void create(Task task) {
        taskContainer.add(task);
    }

    @Override
    public Task read(Long id) {
        return taskContainer.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Task task) {
        for (int i = 0; i < taskContainer.size(); i++) {
            if (taskContainer.get(i).getId().equals(task.getId())) {
                taskContainer.set(i, task);
                return;
            }
        }
    }

    @Override
    public void delete(Long id) {
        taskContainer.removeIf(t -> t.getId().equals(id));
    }
}
