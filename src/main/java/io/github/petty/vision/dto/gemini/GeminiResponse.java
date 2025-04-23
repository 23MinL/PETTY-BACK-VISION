package io.github.petty.vision.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResponse(
        List<Candidate> candidates)
{
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(Content content){}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(List<Part> parts){}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text){}

    public String plainText(){
        if(candidates==null||candidates.isEmpty()) return "";
        return candidates.get(0).content.parts.stream()
                .map(Part::text).reduce("", String::concat);
    }
}