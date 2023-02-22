package com.tetkole.restservice.models;

import jakarta.persistence.*;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy="document")
    private List<Annotation> annotations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "corpus_id")
    private Corpus corpus;

    public Document() { }

    public Document(EDocumentType type, String name, Corpus corpus) {
        this.type = type;
        this.name = name;
        this.corpus = corpus;
    }


    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("docId", this.docId);
        json.put("type", this.type);
        json.put("name", this.name);
        json.put("corpusId", this.corpus.getCorpusId());
        JSONArray annotations_json = new JSONArray(annotations);
        json.put("annotations", annotations_json);

        return json;
    }
}
