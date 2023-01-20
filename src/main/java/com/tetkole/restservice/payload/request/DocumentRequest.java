package com.tetkole.restservice.payload.request;

import com.tetkole.restservice.models.EDocumentType;

import javax.validation.constraints.NotBlank;

public class DocumentRequest {

    @NotBlank
    private String docName;

    @NotBlank
    private EDocumentType type;

    public String getDocName() {
        return docName;
    }

    public EDocumentType getType() {
        return type;
    }
}
