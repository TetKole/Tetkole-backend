package com.tetkole.restservice.models;

import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public Integer getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(Integer annotationId) {
        this.annotationId = annotationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("annotationId", this.annotationId);
        json.put("name", this.name);
        json.put("author", this.author.getMail());
        return json;
    }
}
