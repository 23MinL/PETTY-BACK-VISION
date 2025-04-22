package io.github.petty.vision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RekognitionClient rekognitionClient;

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${together.api.key}")
    private String togetherApiKey;
    @Value("${together.api.url}")
    private String togetherApiUrl;

    public String getInterimMessage(MultipartFile file, String petName) {
        try {
            byte[] imageData = file.getBytes();
            String species = detectSpecies(imageData);

            if ("알 수 없음".equals(species)) {
                return String.format("'%s'에 대해서 알아볼게요! \n잠시만 기다려 주세요. 보고서를 작성 중입니다...", petName);
            }
            return String.format("오 '%s'는 '%s'이 군요!\n잠시만 기다려 주세요. 보고서를 작성 중입니다...", petName, species);
        } catch (Exception e) {
            log.error("AWS Rekognition 오류", e);
            return "AWS 분석 중 오류가 발생했습니다.";
        }
    }

    public String createFinalReport(MultipartFile file, String petName) {
        byte[] imageData;
        try {
            imageData = file.getBytes();
        } catch (Exception e) {
            log.error("이미지 바이트 변환 실패", e);
            return "이미지를 처리할 수 없습니다.";
        }

        String species = detectSpecies(imageData);
        String interim = "알 수 없음".equals(species) ?
                String.format("'%s'에 대해서 알아볼게요! \n잠시만 기다려 주세요. 보고서를 작성 중입니다...", petName) :
                String.format("오 '%s'는 '%s'이 군요!\n잠시만 기다려 주세요. 보고서를 작성 중입니다...", petName, species);

        try {
            return callGemini(imageData, petName, species);
        } catch (Exception gex) {
            log.warn("Gemini 실패 → Together fallback", gex);
        }

        try {
            return callTogetherVision(imageData, petName);
        } catch (Exception tex) {
            log.error("Together 실패", tex);
            return interim + "\n\n최종 분석 보고서 생성 실패했습니다.";
        }
    }

    private static final Map<String, String> SPECIES_KEYWORDS = Map.ofEntries(
            Map.entry("cat", "고양이"),
            Map.entry("kitten", "고양이"),
            Map.entry("dog", "개"),
            Map.entry("puppy", "개"),
            Map.entry("golden retriever", "개"),
            Map.entry("pug", "개"),
            Map.entry("labrador", "개"),
            Map.entry("bird", "새"),
            Map.entry("parrot", "새"),
            Map.entry("canary", "새"),
            Map.entry("rabbit", "토끼"),
            Map.entry("bunny", "토끼"),
            Map.entry("hamster", "햄스터"),
            Map.entry("guinea pig", "기니피그"),
            Map.entry("ferret", "페럿"),
            Map.entry("hedgehog", "고슴도치")
    );

    public String detectSpecies(byte[] imageData) {
        DetectLabelsResponse resp = rekognitionClient.detectLabels(
                DetectLabelsRequest.builder()
                        .image(Image.builder().bytes(SdkBytes.fromByteArray(imageData)).build())
                        .maxLabels(20)
                        .build()
        );

        List<String> matched = new ArrayList<>();
        for (Label label : resp.labels()) {
            if (label.name() == null) continue; // null 예외 방지 추가
            String name = label.name().toLowerCase();
            for (String keyword : SPECIES_KEYWORDS.keySet()) {
                if (name.contains(keyword)) {
                    matched.add(SPECIES_KEYWORDS.get(keyword));
                }
            }
        }

        if (!matched.isEmpty()) {
            return matched.stream().distinct().collect(Collectors.joining(" + "));
        }

        return translateLabel(resp.labels().stream()
                .map(Label::name)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("알 수 없음"));
    }

    private String translateLabel(String label) {
        return SPECIES_KEYWORDS.getOrDefault(label.toLowerCase(), "알 수 없음");
    }

    private String callGemini(byte[] imageData, String petName, String species) {
        String prompt = createEnhancedPromptForReport(petName, species);
        String base64Image = Base64.getEncoder().encodeToString(imageData);

        Map<String,Object> payload = Map.of(
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
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        HttpEntity<Map<String,Object>> req = new HttpEntity<>(payload, headers);
        ResponseEntity<String> res = restTemplate.postForEntity(url, req, String.class);

        if (res.getStatusCode() != HttpStatus.OK) {
            log.error("Gemini API 요청 실패: {} / {}", res.getStatusCode(), res.getBody());
            throw new RuntimeException("Gemini 응답 오류");
        }

        return extractGeminiReport(res.getBody());
    }

    private Map<String, Object> createTogetherPayload(byte[] imageData, String petName) {
        String base64 = Base64.getEncoder().encodeToString(imageData);
        String model = "meta-llama/Llama-3.2-11B-Vision-Instruct-Turbo-Free";

        Map<String, Object> message = Map.of(
                "role", "user",
                "content", List.of(
                        Map.of("type", "text",
                                "text", String.format("반려동물 '%s' 사진을 분석해 보호자에게 전달할 JSON 보고서를 작성해주세요.", petName)),
                        Map.of("type", "image_data",
                                "image_data", Map.of("format", "jpeg", "data", base64))
                )
        );

        return Map.of(
                "model", model,
                "messages", List.of(message)
        );
    }

    private String callTogetherVision(byte[] imageData, String petName) {
        Map<String, Object> payload = createTogetherPayload(imageData, petName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(togetherApiKey);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);
        ResponseEntity<JsonNode> res = restTemplate.exchange(
                togetherApiUrl,
                HttpMethod.POST,
                req,
                JsonNode.class
        );

        if (res.getStatusCode() == HttpStatus.OK && res.getBody() != null) {
            return res.getBody()
                    .path("choices").get(0)
                    .path("message").path("content")
                    .asText();
        }
        throw new RuntimeException("Together API 비정상 응답: " + res.getStatusCode());
    }

    private String createEnhancedPromptForReport(String petName, String species) {
        return String.format("""
    반려동물 '%s'(종류: %s)에 대한 분석 보고서를 작성해줘.

    요청 항목:
    - 종류 (예: 개, 고양이 등)
    - 품종 (믹스견이라면 추정 근거 포함)
    - 외형 (크기, 털 색, 눈에 띄는 특징)
    - 무게 (1kg~40kg 사이)
    - 맹수 여부 (해당 품종은 맹수인지 판단)
    - 감정 또는 행동 (사진 속에서 추론되는 표정)
    - 기타 특이사항 (목줄, 배경 등)

    보호자가 이해하기 쉬운 문장으로 요약해서 보고서를 작성해줘.
    """, petName, species);
    }

    private String extractGeminiReport(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode parts = root.path("candidates").get(0)
                    .path("content").path("parts");
            StringBuilder sb = new StringBuilder();
            for (JsonNode part : parts) {
                if (part.has("text")) {
                    sb.append(part.get("text").asText());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Gemini 응답 파싱 실패", e);
            return "Gemini 응답 분석 실패";
        }
    }
}
