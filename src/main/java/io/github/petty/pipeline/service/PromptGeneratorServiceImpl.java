package io.github.petty.pipeline.service;

import org.springframework.stereotype.Service;

@Service
public class PromptGeneratorServiceImpl implements PromptGeneratorService {

    @Override
    public String generatePrompt(String extractedPetInfoJson, String location) {
        return String.format("""
                반려동물 JSON 정보:
                %s
                사용자가 입력한 위치:
                %s
                """, extractedPetInfoJson, location);
    }
}
