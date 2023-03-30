package com.tetkole.restservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_corpus_role")
public class UserCorpusRole {

    @EmbeddedId
    UserCorpusKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("corpusId")
    @JoinColumn(name = "corpus_id")
    private Corpus corpus;

    private Role role;

    public UserCorpusRole(User user, Corpus corpus, Role role) {
        this.id =  new UserCorpusKey(user.getUserId(), corpus.getCorpusId());
        this.user = user;
        this.corpus = corpus;
        this.role = role;
    }
}
