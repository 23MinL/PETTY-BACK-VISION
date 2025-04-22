package io.github.petty.pipeline.service;

import org.springframework.stereotype.Service;

@Service
public class PromptGeneratorServiceImpl implements PromptGeneratorService {

    @Override
    public String generatePrompt(String extractedPetInfoJson, String location) {
        return String.format("""
                {
                %s
                "location": "%s"
                }
                """, extractedPetInfoJson, location);
    }
}
