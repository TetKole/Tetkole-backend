package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.EDocumentType;
import com.tetkole.restservice.payload.request.CorpusCreationRequest;
import com.tetkole.restservice.repositories.CorpusRepository;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.utils.FileManager;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corpus")
public class CorpusController {

    @Autowired
    public CorpusRepository corpusRepository;

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
        JSONObject jsonError = new JSONObject();

        // Check if corpus with same name already exist
        if (corpusRepository.existsByName(corpusCreationRequest.getCorpusName())) {
            jsonError.put("Error", "The corpus already exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        // créer un dossier et trouver le chemin relatif
        String corpusName = corpusCreationRequest.getCorpusName();
        fileManager.createCorpusFolder(corpusName);

        // créer dossiers annotations, audio, video et images
        fileManager.createFolder(corpusName, "Annotations");
        fileManager.createFolder(corpusName, "FieldAudio");
        fileManager.createFolder(corpusName, "Images");
        fileManager.createFolder(corpusName, "Videos");

        corpusRepository.save(new Corpus(corpusName));

        Optional<Corpus> corpus = corpusRepository.findTopByOrderByCorpusIdDesc();

        // création de corpus_state.json
        fileManager.createCorpusState(corpus.get());

        return ResponseEntity.ok(corpus);
    }

    /**
     * Deuxième méthode utilisée pour PUSH INIT
     * /api/corpus/{corpusId}/addDocument avec dans le body en form-data les fichiers.
     * Permet d'upload les fichiers 1 par 1 (videos/audios/images/annotations).
     */
    @PostMapping("/{corpusId}/addDocument")
    public ResponseEntity<?> addDocument(@Valid @PathVariable Integer corpusId,
                                         @RequestParam(name = "type") EDocumentType type,
                                         @RequestParam(name = "file") MultipartFile file)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Corpus> corpus = corpusRepository.findOneByCorpusId(corpusId);

        if(corpus.isEmpty()) {
            jsonError.put("Error", "The corpus doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }
        String corpusName = corpus.get().getName();

        if (corpusRepository.existsDocumentByName(file.getOriginalFilename())) {
            jsonError.put("Error", "The document already exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        String path = corpusName + "/" + type;

        if(!fileManager.createMultipartFile(path, file)) {
            jsonError.put("Error", "The document could not be uploaded");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        fileManager.createFolder(corpusName + "/Annotations", file.getOriginalFilename());

        documentRepository.save( new Document( type, file.getOriginalFilename(), corpus.get() ) );

        Optional<Document> document = documentRepository.findTopByOrderByDocIdDesc();

        // Fill the corpus_state
        fileManager.addDocumentInCorpusState(document.get());

        return ResponseEntity.ok(document.get().toJson().toString());
    }

    /* -- END PUSH INIT -- */

    /* -- CORPUS CLONE --*/

    @GetMapping("/{id}/clone")
    public ResponseEntity<?> getCorpusState(@Valid @PathVariable Integer id)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Corpus> corpus = corpusRepository.findOneByCorpusId(id);
        if(corpus.isEmpty()) {
            jsonError.put("Error", "The corpus doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        JSONObject corpus_state = fileManager.getCorpusStateContent(corpus.get().getName());
        if (corpus_state == null){
            jsonError.put("Error", "Corpus state not found");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        System.out.println(corpus_state);

        return ResponseEntity.ok(corpus_state.toString());
    }

    /* -- END CLONE -- */

    /* -- CORPUS LIST --*/
    @GetMapping ("/list")
    public ResponseEntity<?> getAll()
    {
        //Get all corpus name
        List<String> listCorpus = corpusRepository.getAllCorpusName();
        if(listCorpus.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(listCorpus,HttpStatus.OK);
    }
    /* -- END CORPUS LIST --*/

}
