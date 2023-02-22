package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.User;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepositoryImplementation<User, Integer> {
    Optional<User> findOneByEmail(String email);
    Boolean existsByEmail(String email);
}
