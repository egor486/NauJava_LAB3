package ru.Dovgan_Egor.NauJava.DAO_PCK;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.User;

import java.util.Date;
import java.util.List;

@Repository
public class TaskRepositoryImpl implements TaskRepositoryCustom{

    private final EntityManager entityManager;

    @Autowired
    public TaskRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public List<Task> findByDtBegBetween(Date start, Date end)
    {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);

        Root<Task> taskRoot = criteriaQuery.from(Task.class);
        Predicate betweenPredicate = criteriaBuilder.between(taskRoot.get("dt_beg"), start, end);

        criteriaQuery.select(taskRoot).where(betweenPredicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Task> findTasksByUserLogin(String login)
    {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);

        Root<Task> taskRoot = criteriaQuery.from(Task.class);
        Join<Task, User> userJoin = taskRoot.join("user_id", JoinType.INNER);
        Predicate loginPredicate = criteriaBuilder.equal(userJoin.get("login"), login);

        criteriaQuery.select(taskRoot).where(loginPredicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
