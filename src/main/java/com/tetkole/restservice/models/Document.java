package com.tetkole.restservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.json.JSONObject;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "corpus_id")
    private Corpus corpus;

    public Document() {
    }

    public Document(EDocumentType type, String name, Corpus corpus) {
        this.type = type;
        this.name = name;
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

    public Corpus getCorpus() {
        return corpus;
    }


    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("docId", this.docId);
        json.put("type", this.type);
        json.put("name", this.name);
        json.put("corpusId", this.corpus.getCorpusId());

        return json;
    }
}
