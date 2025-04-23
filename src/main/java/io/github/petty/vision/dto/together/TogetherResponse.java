package io.github.petty.vision.dto.together;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TogetherResponse {

    private List<Choice> choices;

    @JsonIgnore
    public JsonNode raw() {      // ← TogetherClientImpl 에서 사용
        return new ObjectMapper().valueToTree(this);
    }

    public String plainText() {
        if (choices == null || choices.isEmpty()) return "";
        return choices.get(0).getMessage().getContent();
    }

    @Getter @Setter
    public static class Choice {
        private Message message;
    }

    @Getter @Setter
    public static class Message {
        private String content;
    }
}
