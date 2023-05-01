package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Annotation;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.Role;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.payload.request.RenameRequest;
import com.tetkole.restservice.payload.response.SuccessResponse;
import com.tetkole.restservice.repositories.AnnotationRepository;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.repositories.UserRepository;
import com.tetkole.restservice.utils.FileManager;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> optUser =  userRepository.findOneByEmail(username);

        if(optUser.isEmpty()) {
            jsonError.put("Error", "You are not connected");
            return ResponseEntity
                    .status(401)
                    .body(jsonError.toString());
        }

        int corpusId = document.get().getCorpus().getCorpusId();

        if(!optUser.get().hasAccessToCorpus(corpusId) || optUser.get().hasAccessToCorpus(corpusId, Role.READER)) {
            jsonError.put("Error", "You are not authorized to do that");
            return ResponseEntity
                    .status(403)
                    .body(jsonError.toString());
        }

        String folderPath = document.get().getCorpus().getName() + "/Annotations/" + document.get().getName();

        fileManager.createFolder(folderPath, audioFile.getOriginalFilename());

        String path = folderPath + "/" + audioFile.getOriginalFilename();

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            jsonError.put("Error", "The user doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

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

    @DeleteMapping("{docID}/{annotationId}")
    public ResponseEntity<?> deleteAnnotation(@Valid @PathVariable Integer docID,
                                              @Valid @PathVariable Integer annotationId)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Document> document = documentRepository.findOneByDocId(docID);

        if(document.isEmpty()) {
            jsonError.put("Error", "The document doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> optUser =  userRepository.findOneByEmail(username);

        if(optUser.isEmpty()) {
            jsonError.put("Error", "You are not connected");
            return ResponseEntity
                    .status(401)
                    .body(jsonError.toString());
        }

        int corpusId = document.get().getCorpus().getCorpusId();

        if(!optUser.get().hasAccessToCorpus(corpusId) || optUser.get().hasAccessToCorpus(corpusId, Role.READER)) {
            jsonError.put("Error", "You are not authorized to do that");
            return ResponseEntity
                    .status(403)
                    .body(jsonError.toString());
        }

        Optional<Annotation> annotation = annotationRepository.findById(annotationId);
        if (annotation.isEmpty()) {
            jsonError.put("Error", "The annotation doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        // delete from database
        annotationRepository.deleteById(annotationId);

        // delete from file system
        String folderPath = document.get().getCorpus().getName() + "/Annotations/" + document.get().getName() + "/" + annotation.get().getName();
        boolean success = fileManager.deleteAnnotationFromFolder(folderPath);

        if (success) {
            fileManager.deleteAnnotationInCorpusState(annotation.get());
        }

        return ResponseEntity.ok(new SuccessResponse(success));
    }

    @DeleteMapping("{docID}")
    public ResponseEntity<?> deleteDocument(@Valid @PathVariable Integer docID)
    {
        // please be sure that the doc do not have annotation !
        JSONObject jsonError = new JSONObject();

        Optional<Document> document = documentRepository.findOneByDocId(docID);

        if(document.isEmpty()) {
            jsonError.put("Error", "The document doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        if (document.get().getAnnotations().size() != 0) {
            jsonError.put("Error", "The document has annotations");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> optUser =  userRepository.findOneByEmail(username);

        if(optUser.isEmpty()) {
            jsonError.put("Error", "You are not connected");
            return ResponseEntity
                    .status(401)
                    .body(jsonError.toString());
        }

        int corpusId = document.get().getCorpus().getCorpusId();

        if(!optUser.get().hasAccessToCorpus(corpusId) || optUser.get().hasAccessToCorpus(corpusId, Role.READER)) {
            jsonError.put("Error", "You are not authorized to do that");
            return ResponseEntity
                    .status(403)
                    .body(jsonError.toString());
        }

        // delete from database
        documentRepository.deleteById(docID);

        // delete from file system
        String docPath = document.get().getCorpus().getName() + "/" + document.get().getType();
        boolean success = fileManager.deleteFile(docPath, document.get().getName());

        if (!success) {
            jsonError.put("Error", "Couldn't delete document");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        String annotationPath = document.get().getCorpus().getName() + "/Annotations";
        success = fileManager.deleteFile(annotationPath, document.get().getName());

        if (success) {
            fileManager.deleteDocumentInCorpusState(document.get());
        }

        return ResponseEntity.ok(new SuccessResponse(success));
    }

    @PutMapping("annotation/{annotationId}")
    public ResponseEntity<?> renameAnnotation(@Valid @PathVariable Integer annotationId,
                                              @RequestBody RenameRequest renameRequest)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Annotation> annotation = annotationRepository.findById(annotationId);

        if (annotation.isEmpty()) {
            jsonError.put("Error", "The annotation doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> optUser =  userRepository.findOneByEmail(username);

        if(optUser.isEmpty()) {
            jsonError.put("Error", "You are not connected");
            return ResponseEntity
                    .status(401)
                    .body(jsonError.toString());
        }

        int corpusId = annotation.get().getDocument().getCorpus().getCorpusId();

        if(!optUser.get().hasAccessToCorpus(corpusId) || optUser.get().hasAccessToCorpus(corpusId, Role.READER)) {
            jsonError.put("Error", "You are not authorized to do that");
            return ResponseEntity
                    .status(403)
                    .body(jsonError.toString());
        }

        // TODO catch si ça marche pas et faire une réponse que ça a pas marché
        fileManager.renameAnnotation(annotation.get(), renameRequest.newName());

        annotation.get().setName(renameRequest.newName());

        annotationRepository.save(annotation.get());

        return ResponseEntity.ok(new SuccessResponse(true));
    }

    @PutMapping("{docID}")
    public ResponseEntity<?> renameDoc(@Valid @PathVariable Integer docID,
                                              @RequestBody RenameRequest renameRequest)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Document> document = documentRepository.findOneByDocId(docID);

        if(document.isEmpty()) {
            jsonError.put("Error", "The document doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> optUser =  userRepository.findOneByEmail(username);

        if(optUser.isEmpty()) {
            jsonError.put("Error", "You are not connected");
            return ResponseEntity
                    .status(401)
                    .body(jsonError.toString());
        }

        int corpusId = document.get().getCorpus().getCorpusId();

        if(!optUser.get().hasAccessToCorpus(corpusId) || optUser.get().hasAccessToCorpus(corpusId, Role.READER)) {
            jsonError.put("Error", "You are not authorized to do that");
            return ResponseEntity
                    .status(403)
                    .body(jsonError.toString());
        }

        // TODO catch si ça marche pas et faire une réponse que ça a pas marché
        fileManager.renameDocument(document.get(), renameRequest.newName());

        document.get().setName(renameRequest.newName());

        documentRepository.save(document.get());

        return ResponseEntity.ok(new SuccessResponse(true));
    }


    @GetMapping("/name/{name}")
    public ResponseEntity<?> getDocIdByName(@Valid @PathVariable String name)
    {
        // please be sure that the doc do not have annotation !
        JSONObject jsonError = new JSONObject();

        Optional<Document> document = documentRepository.findOneByName(name);

        if(document.isEmpty()) {
            jsonError.put("Error", "The document doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        return ResponseEntity.ok(document.get().getDocId());
    }
}
