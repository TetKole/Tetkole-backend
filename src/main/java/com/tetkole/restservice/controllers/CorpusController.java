package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.payload.request.CorpusCreationRequest;
import com.tetkole.restservice.payload.request.DocumentRequest;
import com.tetkole.restservice.repositories.CorpusRespository;
import com.tetkole.restservice.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/corpus")
public class CorpusController {

    @Autowired
    public CorpusRespository corpusRepository;

    @Autowired
    public DocumentRepository documentRepository;

    @PostMapping()
    public ResponseEntity<?> addCorpus(@Valid @RequestBody CorpusCreationRequest corpusCreationRequest)
    {
        if (corpusRepository.existsByName(corpusCreationRequest.getCorpusName())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: There is already a coprus with the name : " + corpusCreationRequest.getCorpusName() +"!");
        }

        //TODO créer un dossier et trouver le chemin relatif
        String uri = corpusCreationRequest.getCorpusName();
        //TODO créer dossiers annotations, audio, video et images
        //TODO création de corpus_state.json

        corpusRepository.save(new Corpus(corpusCreationRequest.getCorpusName(), uri));

        Optional<Corpus> corpus = corpusRepository.findTopByOrderByIdDesc();

        return ResponseEntity.ok(corpus);
    }

    @PostMapping("/{corpusId}/addDocument")
    public ResponseEntity<?> addDocument(@Valid @RequestBody DocumentRequest documentRequest, @PathVariable int corpusId)
    {
        if (documentRepository.existsByNameAndCorpusId(documentRequest.getDocName(), corpusId)) {
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

        Optional<Document> document = documentRepository.findTopByOrderByIdDesc();

        return ResponseEntity.ok(document);
    }

    @PostMapping("/{corpusId}/document/{docId}/addAnnotation")
    public ResponseEntity<?> addAnnotation(@Valid @RequestBody CorpusCreationRequest corpusCreationRequest,
            @PathVariable int corpusId,
            @PathVariable int docId)
    {
        //TODO penser s'il serait mieux de stocker dans la BDD ou bien dans un JSON

        return ResponseEntity.ok("Corpus created successfully!");
    }

}
