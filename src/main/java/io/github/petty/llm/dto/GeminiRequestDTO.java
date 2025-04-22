package io.github.petty.llm.dto;

import io.github.petty.tour.entity.Content;

import java.util.List;

public record GeminiRequestDTO(List<Content> contents) {
    public record Content(List<Part> parts) {}
    public record Part(String text) {}
}
