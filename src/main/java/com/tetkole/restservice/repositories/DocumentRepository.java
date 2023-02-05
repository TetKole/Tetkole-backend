package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.Document;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepositoryImplementation<Document, Integer> {
    Optional<Document> findTopByOrderByDocIdDesc();
    Optional<Document> findOneByDocId(Integer docId);
    Optional<Document> findOneByName(String docName);
}