package ru.Dovgan_Egor.NauJava.MODEL.DAO_PCK;

import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;

import java.util.Date;
import java.util.List;

public interface TaskRepositoryCustom {

    List<Task> findByDtBegBetween(Date start, Date end);

    List<Task> findTasksByUserLogin(String login);
}
