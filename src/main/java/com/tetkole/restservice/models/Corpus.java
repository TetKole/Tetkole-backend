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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "corpus")
    private List<Document> documents = new ArrayList<>();

    public Corpus(String name) {
        this.name = name;
    }

    public Corpus() { }

    public Integer getCorpusId() {
        return corpusId;
    }

    public String getName() {
        return name;
    }

    public List<Document> getDocuments() {
        return documents;
    }
}
