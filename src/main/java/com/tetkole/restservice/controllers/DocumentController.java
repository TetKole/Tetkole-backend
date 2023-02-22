package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Annotation;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.repositories.AnnotationRepository;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.repositories.UserRepository;
import com.tetkole.restservice.utils.FileManager;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocumentController {

    public final DocumentRepository documentRepository;
    public final AnnotationRepository annotationRepository;
    public final UserRepository userRepository;
    public final FileManager fileManager;

    @PostMapping("{docID}/addAnnotation")
    public ResponseEntity<?> addAnnotation(@Valid @PathVariable Integer docID,
                                           @RequestParam(name = "userId") int userId,
                                           @RequestParam(name = "audioFile") MultipartFile audioFile,
                                           @RequestParam(name = "jsonFile") MultipartFile jsonFile)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Document> document = documentRepository.findOneByDocId(docID);

        if(document.isEmpty()) {
            jsonError.put("Error", "The document doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        String folderPath = document.get().getCorpus().getName() + "/Annotations/" + document.get().getName();

        fileManager.createFolder(folderPath, audioFile.getOriginalFilename());

        String path = folderPath + "/" + audioFile.getOriginalFilename();

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

        if(annotationRepository.existsByNameAndDocument(audioFile.getOriginalFilename(), document.get())) {
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
