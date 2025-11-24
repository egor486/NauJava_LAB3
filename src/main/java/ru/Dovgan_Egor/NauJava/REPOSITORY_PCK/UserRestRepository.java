package ru.Dovgan_Egor.NauJava.REPOSITORY_PCK;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.User;

import java.util.List;

public interface UserRestRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);

    List<User> findByRoleAndLogin(String role, String login);

    List<User> findByLogin(String login);
}
