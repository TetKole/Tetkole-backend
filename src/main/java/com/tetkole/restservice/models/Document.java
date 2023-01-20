package com.tetkole.restservice.models;

import jakarta.persistence.*;

@Entity
@Table(name= "document")
public class Document {

    @Column(name="doc_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="type",nullable=false, length = 20)
    @Enumerated(EnumType.STRING)
    private EDocumentType type;

    @Column(name="name",nullable=false)
    private String name;

    @Column(name="uri",nullable=false)
    private String uri;

    public Document() {
    }

    public Document(EDocumentType type, String name, String uri) {
        this.type = type;
        this.name = name;
        this.uri = uri;
    }
}
