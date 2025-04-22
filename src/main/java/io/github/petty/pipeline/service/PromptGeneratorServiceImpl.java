package io.github.petty.pipeline.service;

import org.springframework.stereotype.Service;

@Service
public class PromptGeneratorServiceImpl implements PromptGeneratorService {

    @Override
    public String generatePrompt(String extractedPetInfoJson, String location) {
        if (extractedPetInfoJson.isEmpty()) {
            throw new IllegalArgumentException("에러! 필수 정보가 없습니다.");
        } else if (location.isEmpty()) {
            throw new IllegalArgumentException("에러! 사용자 위치 정보가 없습니다.");
        }
        return String.format("""
                {
                %s
                "location": "%s"
                }
                """, extractedPetInfoJson, location);
    }
}
