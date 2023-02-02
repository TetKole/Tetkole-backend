package com.tetkole.restservice.payload.request;

import com.tetkole.restservice.models.EDocumentType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public class DocumentRequest {
    @NotBlank
    private String fileName;

    @NotBlank
    private EDocumentType type;

    @NotBlank
    private MultipartFile file;


    public String getFileName() {
        return fileName;
    }

    public EDocumentType getType() {
        return type;
    }

    public MultipartFile getFile() {
        return file;
    }
}
