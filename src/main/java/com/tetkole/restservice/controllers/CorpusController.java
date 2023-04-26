package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.*;
import com.tetkole.restservice.payload.request.CorpusAddNewUserRequest;
import com.tetkole.restservice.payload.request.CorpusCreationRequest;
import com.tetkole.restservice.payload.response.UserDTO;
import com.tetkole.restservice.repositories.CorpusRepository;
import com.tetkole.restservice.repositories.DocumentRepository;
import com.tetkole.restservice.repositories.UserCorpusRoleRepository;
import com.tetkole.restservice.repositories.UserRepository;
import com.tetkole.restservice.utils.FileManager;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corpus")
@RequiredArgsConstructor
public class CorpusController {

    public final CorpusRepository corpusRepository;
    public final DocumentRepository documentRepository;
    public  final UserRepository userRepository;
    public final UserCorpusRoleRepository userCorpusRoleRepository;
    public final FileManager fileManager;


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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> user =  userRepository.findOneByEmail(username);

        JSONObject jsonError = new JSONObject();

        if(user.isEmpty()) {
            jsonError.put("Error", "The user doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        // Check if corpus with same name already exist
        if (corpusRepository.existsByName(corpusCreationRequest.corpusName())) {
            jsonError.put("Error", "The corpus already exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        // créer un dossier et trouver le chemin relatif
        String corpusName = corpusCreationRequest.corpusName();
        fileManager.createCorpusFolder(corpusName);

        // créer dossiers annotations, audio, video et images
        fileManager.createFolder(corpusName, "Annotations");
        fileManager.createFolder(corpusName, "FieldAudio");
        fileManager.createFolder(corpusName, "Images");
        fileManager.createFolder(corpusName, "Videos");

        Corpus corpus = new Corpus(corpusName);
        corpusRepository.save(corpus);
        userCorpusRoleRepository.save(new UserCorpusRole(user.get(), corpus, Role.MODERATOR));


        // création de corpus_state.json
        fileManager.createCorpusState(corpus);

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

        return ResponseEntity.ok(corpus_state.toString());
    }
    /* -- END CLONE -- */

    /* -- CORPUS LIST --*/
    @GetMapping ("/list")
    public ResponseEntity<?> getAll()
    {
        //Get all corpus name
        List<Corpus> listCorpus = corpusRepository.findAll();
        JSONArray response = new JSONArray();
        listCorpus.forEach(corpus -> response.put(corpus.toJson()));
        if(listCorpus.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response.toString(),HttpStatus.OK);
    }
    /* -- END CORPUS LIST --*/

    /* -- CORPUS VERSIONNING --*/
    @PostMapping ("/{id}/createVersion")
    public ResponseEntity<?> createVersion(@Valid @PathVariable Integer id)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Corpus> corpus = corpusRepository.findOneByCorpusId(id);
        if(corpus.isEmpty()) {
            jsonError.put("Error", "The corpus doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        // TODO vérifier si le mec connecté est modo ou admin du corpus

        if(!fileManager.createZipVersion(corpus.get())){
            jsonError.put("Error", "The server have a probleme to create a version");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        corpus.get().nextVersion();
        corpusRepository.save(corpus.get());

        return new ResponseEntity<>("Version created",HttpStatus.OK);
    }

    @GetMapping ("/{id}/currentVersion")
    public ResponseEntity<?> getCurrentVersion(@Valid @PathVariable Integer id)
    {
        JSONObject jsonError = new JSONObject();

        Optional<Corpus> corpus = corpusRepository.findOneByCorpusId(id);
        if(corpus.isEmpty()) {
            jsonError.put("Error", "The corpus doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        return ResponseEntity
            .ok()
            .body(corpus.get().getVersion() - 1);
    }
    /* -- END CORPUS VERSIONNING --*/

    @GetMapping ("/{id}/users")
    public ResponseEntity<?> getAllUsers(@Valid @PathVariable Integer id) {
        JSONObject jsonError = new JSONObject();

        Optional<Corpus> corpus = corpusRepository.findOneByCorpusId(id);
        if(corpus.isEmpty()) {
            jsonError.put("Error", "The corpus doesn't exist");
            return ResponseEntity
                    .badRequest()
                    .body(jsonError.toString());
        }

        JSONArray response = new JSONArray();
        if(corpus.get().getUsers().isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        for (UserCorpusRole u : corpus.get().getUsers()) {
            UserDTO tmp = new UserDTO(u.getUser());
            response.put(tmp.toJson());
        }

        return new ResponseEntity<>(response.toString(),HttpStatus.OK);
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<?> addNewUserToCorpus(@Valid @PathVariable Integer id,
                                                @Valid @RequestBody CorpusAddNewUserRequest requestBody) {
        JSONObject jsonError = new JSONObject();

        Optional<Corpus> optCorpus = corpusRepository.findOneByCorpusId(id);

        if(optCorpus.isEmpty()) {
            jsonError.put("Error", "CorpusDoesNotExist");
            return ResponseEntity
                    .status(400)
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

        User user = optUser.get();

        if(!user.isUserAdminOfCorpus(id)) {
            jsonError.put("Error", "You are not authorized to do that");
            return ResponseEntity
                    .status(403)
                    .body(jsonError.toString());
        }

        Optional<User> optUserToAdd = userRepository.findOneByEmail(requestBody.userEmail());

        if(optUserToAdd.isEmpty()) {
            jsonError.put("Error", "This user does not exist in the system yet");
            return ResponseEntity
                    .status(404)
                    .body(jsonError.toString());
        }

        Optional<Role> optRole = Role.getValueFromString(requestBody.userRole());

        if(optRole.isEmpty()) {
            jsonError.put("Error", "This role is not correct");
            return ResponseEntity
                    .status(400)
                    .body(jsonError.toString());
        }

        User userToAdd = optUserToAdd.get();

        if(userCorpusRoleRepository.existsByUserAndCorpus(userToAdd, optCorpus.get())) {
            jsonError.put("Error", "User already in this corpus");
            return ResponseEntity
                    .status(409)
                    .body(jsonError.toString());
        }

        Role role = optRole.get();

        UserCorpusRole userCorpusRole = new UserCorpusRole(userToAdd, optCorpus.get(), role);

        userCorpusRoleRepository.save(userCorpusRole);


        return ResponseEntity
                .ok("User successfully added");

    }
}
