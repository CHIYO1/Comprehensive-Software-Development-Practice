package com.scnu.gpt.pojo.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DifyTextChunk(
        @JsonProperty("text") String text,
        @JsonProperty("from_variable_selector") List<String> fromVariableSelector
) {}