package ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.SubTaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.TaskRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.SubTask;

import java.util.List;

@Service
public class TasksServiceImpl implements TasksService {
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public TasksServiceImpl(TaskRepository taskRepository,
                            SubTaskRepository subTaskRepository,
                            PlatformTransactionManager transactionManager)
    {
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void deleteTaskWithSubTasks(Long taskId){
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {

            List<SubTask> subTasks = subTaskRepository.findByTaskId(taskId);


            for (SubTask subTask : subTasks) {
                subTaskRepository.delete(subTask);
            }

            taskRepository.deleteById(taskId);

            transactionManager.commit(status);
        } catch (DataAccessException ex) {

            transactionManager.rollback(status);
            throw ex;
        }
    }
}
