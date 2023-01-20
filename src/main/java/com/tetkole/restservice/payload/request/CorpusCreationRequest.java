package com.tetkole.restservice.payload.request;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class CorpusCreationRequest {

    @NotBlank
    private String corpusName;

    public String getCorpusName() {
        return corpusName;
    }
}
