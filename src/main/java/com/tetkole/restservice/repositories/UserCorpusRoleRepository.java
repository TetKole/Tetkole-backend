package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.models.UserCorpusRole;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface UserCorpusRoleRepository  extends JpaRepositoryImplementation<UserCorpusRole, Integer> {
    List<Corpus> findAllByUser(User user);
    List<User> findAllByCorpus(Corpus corpus);
}