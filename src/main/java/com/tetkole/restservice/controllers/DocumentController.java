package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.EDocumentType;
import com.tetkole.restservice.payload.request.CorpusCreationRequest;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.utils.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    @Autowired
    public DocumentRepository documentRepository;

    @Autowired
    public FileManager fileManager;

    @PostMapping("/{docId}/addAnnotation")
    public ResponseEntity<?> addAnnotation(@Valid @PathVariable int docId,
                                           @RequestParam(name = "file") MultipartFile file,
                                           @RequestParam(name = "json") MultipartFile json)
    {
        Optional<Document> document = documentRepository.findOneByDocId(docId);
        if(document.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: The document doesn't exist!");
        }

        String path = document.get().getCorpus().getName() + "/" + EDocumentType.Annotations + "/"
                + document.get().getName();

        fileManager.createFolder(path, file.getOriginalFilename());

        path = path + "/" + file.getOriginalFilename();
        System.out.println(path);

        if(!fileManager.createMultipartFile(path, file)) {
            return ResponseEntity
                    .badRequest()
                    .body("Server Error: The document could not be uploaded.");
        };

        if(!fileManager.createMultipartFile(path, json)) {
            return ResponseEntity
                    .badRequest()
                    .body("Server Error: The json could not be uploaded.");
        };

        return ResponseEntity.ok("ok");
    }
}
