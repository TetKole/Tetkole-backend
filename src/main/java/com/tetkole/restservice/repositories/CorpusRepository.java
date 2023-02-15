package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.Corpus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorpusRepository extends JpaRepositoryImplementation<Corpus, Integer> {
    Boolean existsByName(String name);
    Optional<Corpus> findOneByCorpusId(Integer corpusId);
    Boolean existsByCorpusId(Integer id);
    Optional<Corpus> findTopByOrderByCorpusIdDesc();
    Boolean existsDocumentByName(String name);
    List<Corpus> findAll();

    @Query("select name, corpusId from Corpus")
    List<Object> getAllCorpusName();
}