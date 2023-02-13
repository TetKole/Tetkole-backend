package com.tetkole.restservice.repositories;

import com.tetkole.restservice.models.Annotation;
import com.tetkole.restservice.models.Document;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AnnotationRepository extends JpaRepositoryImplementation<Annotation, Integer> {
    Optional<Annotation> findTopByOrderByAnnotationIdDesc();
    Optional<Annotation> findOneByAnnotationId(Integer annotationId);
    Boolean existsByNameAndDocId(String name, Integer docId);
}
