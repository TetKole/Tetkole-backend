package com.tetkole.restservice.models;

import jakarta.persistence.*;
import lombok.Data;
import org.json.JSONObject;

@Data
@Entity
@Table(name= "annotation")
public class Annotation {

    @Column(name="annotation_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer annotationId;

    @Column(name="name",nullable=false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doc_id")
    private Document document;

    public Annotation(String name, User author, Document document) {
        this.name = name;
        this.author = author;
        this.document = document;
    }

    public Annotation() { }


    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("annotationId", this.annotationId);
        json.put("name", this.name);
        json.put("author", this.author.getEmail());
        return json;
    }
}
