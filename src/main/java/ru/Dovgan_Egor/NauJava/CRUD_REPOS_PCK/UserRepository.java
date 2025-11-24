package ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByName(String name);

    List<User> findByRoleAndLogin(String role, String login);

    //List<User> findByLogin(String login);

    Optional<User> findByLogin(String login);

}
