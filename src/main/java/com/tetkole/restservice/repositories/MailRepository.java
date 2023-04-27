package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.Mail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailRepository extends CrudRepository<Mail, Integer> {
    Boolean existsByMail(String mail);
    Optional<Mail> findByMail(String mail);
}
