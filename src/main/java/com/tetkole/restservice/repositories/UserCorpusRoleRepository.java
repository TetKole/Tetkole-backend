package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.models.UserCorpusRole;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;
import java.util.Optional;

public interface UserCorpusRoleRepository  extends JpaRepositoryImplementation<UserCorpusRole, Integer> {
    List<UserCorpusRole> findAllByUser(User user);
    Boolean existsByUserAndCorpus(User user, Corpus corpus);
    Optional<UserCorpusRole> findByUserAndCorpus(User user, Corpus corpus);
}