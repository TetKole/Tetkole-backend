package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.User;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepositoryImplementation<User, Integer> {
    Optional<User> findOneByMail(String mail);
    Boolean existsByMail(String mail);
}
