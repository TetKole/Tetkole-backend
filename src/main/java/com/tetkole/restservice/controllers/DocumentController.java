package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.EDocumentType;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.utils.FileManager;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    @Autowired
    public DocumentRepository documentRepository;

    @Autowired
    public FileManager fileManager;

    @PostMapping("/addAnnotation")
    public ResponseEntity<?> addAnnotation(@RequestParam(name = "documentName") String documentName,
                                           @RequestParam(name = "audioFile") MultipartFile audioFile,
                                           @RequestParam(name = "jsonFile") MultipartFile jsonFile)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Document> document = documentRepository.findOneByName(documentName);

        if(document.isEmpty()) {
            jsonError.put("Error", "The document doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        String folderPath = document.get().getCorpus().getName() + "/" + EDocumentType.Annotations + "/" + documentName;
        System.out.println(folderPath);

        fileManager.createFolder(folderPath, audioFile.getOriginalFilename());

        String path = folderPath + "/" + audioFile.getOriginalFilename();
        System.out.println(path);

        if(!fileManager.createMultipartFile(path, audioFile)) {
            jsonError.put("Error", "The document could not be uploaded");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        if(!fileManager.createMultipartFile(path, jsonFile)) {
            jsonError.put("Error", "The document could not be uploaded");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        documentRepository.save( new Document(
                EDocumentType.Annotations,
                audioFile.getOriginalFilename(),
                document.get().getCorpus())
        );

        Optional<Document> annotation = documentRepository.findTopByOrderByDocIdDesc();

        return ResponseEntity.ok(annotation.get().toJson().toString());
    }
}
