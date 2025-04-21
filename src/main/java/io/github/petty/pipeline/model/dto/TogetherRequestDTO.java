package io.github.petty.pipeline.model.dto;

import java.util.List;

public record TogetherRequestDTO(String model, List<Message> messages) {
    public record Message(String role, String content) {}
}
