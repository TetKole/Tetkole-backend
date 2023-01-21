package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.Corpus;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorpusRespository extends JpaRepositoryImplementation<Corpus, Integer> {
    Boolean existsByName(String name);

    Optional<Corpus> findTopByOrderByCorpusIdDesc();

    Boolean existsDocumentByName(String name);
}