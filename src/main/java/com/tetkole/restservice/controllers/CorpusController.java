package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.EDocumentType;
import com.tetkole.restservice.payload.request.CorpusCreationRequest;
import com.tetkole.restservice.repositories.CorpusRespository;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.utils.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.Optional;

@RestController
@RequestMapping("/api/corpus")
public class CorpusController {

    @Autowired
    public CorpusRespository corpusRepository;

    @Autowired
    public DocumentRepository documentRepository;

    @Autowired
    public FileManager fileManager;


    /* -- PUSH INIT -- */

    /**
     * Première méthode à être utilisée pour PUSH INIT
     * POST /api/corpus avec dans le body en json : {"corpusName": "nouveauCorpus"}
     * permet de créer et préparer le dossier de ce corpus dans le file system du backend.
     * Retourne le corpus, permettant au front de poursuivre en connaissant le corpusId.
     */
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

    /**
     * Deuxième méthode utilisée pour PUSH INIT
     * /api/corpus/{corpusId}/addDocument avec dans le body en form-data les fichiers.
     * Permet d'upload les fichiers 1 par 1 (videos/audios/images/annotations).
     */
    @PostMapping("/{corpusId}/addDocument")
    public ResponseEntity<?> addDocument(@Valid @PathVariable Integer corpusId, @RequestParam(name = "type") EDocumentType type,
                                         @RequestParam(name = "fileName") String fileName,
                                         @RequestParam(name = "file") MultipartFile file)
    {
        Optional<Corpus> corpus = corpusRepository.findOneByCorpusId(corpusId);

        if(corpus.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: The corpus doesn't exist!");
        }

        if (corpusRepository.existsDocumentByName(fileName)) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: There is already a document with the name : " + fileName + " in this corpus!");
        }

        String path = corpus.get().getName() + "/" + type;

        if(!fileManager.createMultipartFile(path, file)) {
            return ResponseEntity
                    .badRequest()
                    .body("Server Error: The document could not be uploaded.");
        };

        fileManager.createFolder(corpus.get().getName() + "/" + EDocumentType.Annotations, fileName);

        documentRepository.save(new Document(type,
                fileName,
                path + "/" + fileName,
                corpus.get())
        );

        Optional<Document> document = documentRepository.findTopByOrderByDocIdDesc();

        return ResponseEntity.ok(document);
    }

    /* -- END PUSH INIT -- */

}
