package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Annotation;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.EDocumentType;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.repositories.AnnotationRepository;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.repositories.UserRepository;
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
    public AnnotationRepository annotationRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public FileManager fileManager;

    @PostMapping("/addAnnotation")
    public ResponseEntity<?> addAnnotation(@RequestParam(name = "documentName") String documentName,
                                           @RequestParam(name = "userId") int userId,
                                           @RequestParam(name = "audioFile") MultipartFile audioFile,
                                           @RequestParam(name = "jsonFile") MultipartFile jsonFile)
    {
        //TODO tester la methode et ajouter id user dans la requête front

        JSONObject jsonError = new JSONObject();

        Optional<Document> document = documentRepository.findOneByName(documentName);

        if(document.isEmpty()) {
            jsonError.put("Error", "The document doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        String folderPath = document.get().getCorpus().getName() + "/Annotations/" + documentName;
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

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            jsonError.put("Error", "The user doesn't exists");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        if(annotationRepository.existsByNameAndDocId(audioFile.getOriginalFilename(), document.get().getDocId())) {
            jsonError.put("Error", "The annotation already exists");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        annotationRepository.save( new Annotation(
                audioFile.getOriginalFilename(),
                user.get(),
                document.get())
        );

        Optional<Annotation> annotation = annotationRepository.findTopByOrderByAnnotationIdDesc();

        // Fill the corpus_state
        fileManager.addAnnotationInCorpusState(annotation.get());

        return ResponseEntity.ok(annotation.get().toJson().toString());
    }
}
