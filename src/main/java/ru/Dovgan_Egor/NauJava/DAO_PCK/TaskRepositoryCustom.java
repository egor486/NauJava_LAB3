package ru.Dovgan_Egor.NauJava.DAO_PCK;

import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;

import java.util.Date;
import java.util.List;

public interface TaskRepositoryCustom {

    List<Task> findByDtBegBetween(Date start, Date end);

    List<Task> findTasksByUserLogin(String login);
}
