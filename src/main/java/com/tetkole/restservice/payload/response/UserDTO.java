package com.tetkole.restservice.payload.response;

import com.tetkole.restservice.models.Role;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.models.UserCorpusRole;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {
    private Integer userId;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
    List<String> corpus;

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.corpus = new ArrayList<>();
        for (UserCorpusRole userCorpusRole: user.getCorpus()) {
            this.corpus.add(userCorpusRole.getCorpus().getName());
        }
    }
}
