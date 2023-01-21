package com.tetkole.restservice.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "corpus")
public class Corpus {

    @Column(name="corpus_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer corpusId;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="uri", nullable=false)
    private String uri;

    @OneToMany
    @JoinColumn(name="corpus_id")
    private List<Document> documents = new ArrayList<>();

    public Corpus(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public Corpus() {

    }

    public Integer getCorpusId() {
        return corpusId;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public List<Document> getDocuments() {
        return documents;
    }
}
