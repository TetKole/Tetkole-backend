package com.tetkole.restservice.models;

import jakarta.persistence.*;
import lombok.Data;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
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


    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("corpusId", this.corpusId);
        return json;
    }
}
