package io.github.petty.pipeline.service;

public interface PromptGeneratorService {
    String generatePrompt(String extractedPetInfoJson, String location);
}
