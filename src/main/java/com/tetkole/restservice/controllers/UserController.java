package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.models.UserCorpusRole;
import com.tetkole.restservice.repositories.CorpusRepository;
import com.tetkole.restservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    public  final UserRepository userRepository;
    public  final CorpusRepository corpusRepository;

    @GetMapping("{userId}/corpus")
    public ResponseEntity<?> getAllCorpusOfUser(
            @Valid @PathVariable Integer userId
    ) {
        JSONObject jsonError = new JSONObject();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        Optional<User> optUser =  userRepository.findOneByEmail(username);

        if(optUser.isEmpty()) {
            jsonError.put("Error", "You are not connected");
            return ResponseEntity
                    .status(401)
                    .body(jsonError.toString());
        }

        if(!Objects.equals(optUser.get().getUserId(), userId)) {
            jsonError.put("Error", "You are trying to access another user's resources");
            return ResponseEntity
                    .status(403)
                    .body(jsonError.toString());
        }

        JSONArray response = new JSONArray();

        for(UserCorpusRole corpusRole : optUser.get().getCorpus()) {
            response.put(corpusRole.getCorpus().toJson());
        }

        if(response.isEmpty()) {
            return ResponseEntity
                    .status(204)
                    .build();
        }

        return ResponseEntity.ok(response.toString());
    }
}
