package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.payload.request.CorpusCreationRequest;
import com.tetkole.restservice.payload.request.DocumentRequest;
import com.tetkole.restservice.repositories.CorpusRespository;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.utils.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.Optional;

@RestController
@RequestMapping("/corpus")
public class CorpusController {

    @Autowired
    public CorpusRespository corpusRepository;

    @Autowired
    public DocumentRepository documentRepository;

    @Autowired
    public FileManager fileManager;

    @PostMapping()
    public ResponseEntity<?> addCorpus(@Valid @RequestBody CorpusCreationRequest corpusCreationRequest)
    {
        // Check if corpus with same name already exist
        if (corpusRepository.existsByName(corpusCreationRequest.getCorpusName())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: There is already a coprus with the name : " + corpusCreationRequest.getCorpusName() +"!");
        }

        // créer un dossier et trouver le chemin relatif
        String corpusName = corpusCreationRequest.getCorpusName();
        fileManager.createCorpusFolder(corpusName);

        // créer dossiers annotations, audio, video et images
        fileManager.createFolder(corpusName, "Annotations");
        fileManager.createFolder(corpusName, "FieldAudio");
        fileManager.createFolder(corpusName, "Images");
        fileManager.createFolder(corpusName, "Videos");

        // création de corpus_state.json
        File corpusStateFile = fileManager.createFile(corpusName, "corpus_state.json");
        corpusRepository.save(new Corpus(corpusCreationRequest.getCorpusName(), corpusName));

        Optional<Corpus> corpus = corpusRepository.findTopByOrderByCorpusIdDesc();

        return ResponseEntity.ok(corpus);
    }

    @PostMapping("/{corpusId}/addDocument")
    public ResponseEntity<?> addDocument(@Valid @RequestBody DocumentRequest documentRequest, @PathVariable int corpusId)
    {
        if (corpusRepository.existsDocumentByName(documentRequest.getDocName())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: There is already a coprus with the name : " + documentRequest.getDocName() + " in this corpus!");
        }


        //TODO déplacer le fichier de la requete
        //TODO créer un dossier pour ce document dans annotation
        String uri = documentRequest.getDocName();
        documentRepository.save(new Document(documentRequest.getType(),
                documentRequest.getDocName(),
                uri)
        );

        Optional<Document> document = documentRepository.findTopByOrderByDocIdDesc();

        return ResponseEntity.ok(document);
    }

    @PostMapping("/{corpusId}/document/{docId}/addAnnotation")
    public ResponseEntity<?> addAnnotation(@Valid @RequestBody CorpusCreationRequest corpusCreationRequest,
            @PathVariable int corpusId,
            @PathVariable int docId)
    {
        //TODO refléchir s'il serait mieux de stocker dans la BDD ou bien dans un JSON

        return ResponseEntity.ok("Corpus created successfully!");
    }

}
