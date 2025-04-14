package io.github.petty.vision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisionService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public String createPetReport(String petName, byte[] imageData, List<String> rekognitionLabels, List<String> googleVisionLabels) {
        try {
            String prompt = createEnhancedPromptForReport(petName, rekognitionLabels, googleVisionLabels);
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            Map<String, Object> payload = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(
                                    Map.of("text", prompt),
                                    Map.of("inline_data", Map.of(
                                            "mime_type", "image/jpeg",
                                            "data", base64Image
                                    ))
                            )
                    ))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ 이 부분이 핵심 수정 부분입니다.
            String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Gemini API 요청 실패: Status = {}, Body = {}", response.getStatusCode(), response.getBody());
                return "Gemini 분석 요청 실패";
            }

            return extractReport(petName, response.getBody());

        } catch (Exception e) {
            log.error("Gemini 보고서 생성 실패", e);
            return "Gemini 분석 중 오류 발생";
        }
    }

    public String analyzeImage(MultipartFile multipartFile, String promptText) {
        try {
            byte[] imageData = multipartFile.getBytes();
            return createPetReport(promptText, imageData, List.of(), List.of()); // 라벨 없이 실행
        } catch (IOException e) {
            log.error("MultipartFile 변환 실패", e);
            return "이미지를 처리할 수 없습니다.";
        }
    }

    private String createEnhancedPromptForReport(String petName, List<String> rekognitionLabels, List<String> googleVisionLabels) {
        return String.format("""
            반려동물 '%s'에 대한 분석 보고서를 작성해줘. 아래는 AWS Rekognition 및 Google Vision API를 통해 얻은 라벨 정보야.

            ## AWS Rekognition:
            %s

            ## Google Vision:
            %s

            요청 항목:
            - 종류 (예: 개, 고양이 등)
            - 품종 (믹스견이라면 추정 근거 포함)
            - 외형 (크기, 털 색, 눈에 띄는 특징)
            - 감정 또는 행동 (사진 속에서 추론되는 표정)
            - 기타 특이사항 (목줄, 배경 등)

            이 내용을 바탕으로 보호자가 이해하기 쉬운 보고서를 작성해줘.
            (요청 ID: %s)
        """,
                petName,
                String.join(", ", rekognitionLabels),
                String.join(", ", googleVisionLabels),
                UUID.randomUUID());
    }

    private String extractReport(String petName, String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                log.warn("[{}] Gemini 응답 candidates 없음: {}", petName, jsonResponse);
                return "Gemini 응답 형식이 예상과 다릅니다.";
            }

            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                log.warn("[{}] Gemini 응답 parts 배열 없음: {}", petName, jsonResponse);
                return "Gemini 응답이 예상과 다릅니다. 수동 확인이 필요합니다.";
            }

            StringBuilder report = new StringBuilder();
            for (JsonNode part : parts) {
                if (part.has("text")) {
                    report.append(part.get("text").asText());
                }
            }
            return report.toString();

        } catch (Exception e) {
            log.error("[{}] Gemini 응답 파싱 실패", petName, e);
            return "Gemini 응답 분석 실패";
        }
    }
}
