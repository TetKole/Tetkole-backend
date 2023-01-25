package com.tetkole.restservice.models;

import jakarta.persistence.*;

@Entity
@Table(name= "document")
public class Document {

    @Column(name="doc_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer docId;

    @Column(name="type",nullable=false, length = 20)
    @Enumerated(EnumType.STRING)
    private EDocumentType type;

    @Column(name="name",nullable=false)
    private String name;

    @Column(name="uri",nullable=false)
    private String uri;

    @OneToOne
    @JoinColumn(name = "corpus_id")
    private Corpus corpus;

    public Document() {
    }

    public Document(EDocumentType type, String name, String uri, Corpus corpus) {
        this.type = type;
        this.name = name;
        this.uri = uri;
        this.corpus = corpus;
    }

    public Integer getDocId() {
        return docId;
    }

    public EDocumentType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public Corpus getCorpus() {
        return corpus;
    }
}
