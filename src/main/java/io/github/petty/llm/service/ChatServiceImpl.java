package io.github.petty.llm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.petty.llm.dto.GeminiRequestDTO;
import io.github.petty.llm.dto.GeminiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class ChatServiceImpl implements ChatService{
    @Value("${gemini.api.key}")
    private String apiKey;

    @Override
    public String generateFromPrompt(String prompt, List<Document> docs) throws Exception {
        // 추천 받은 장소 정보
        String recommend = docs.stream()
                .map(doc -> doc.getMetadata().get("title")+": "+ doc.getText())
                .collect(Collectors.joining("\n"));

        String finalPrompt = """
                사용자의 질문은 CONTEXT로 제공됩니다.
                아래 RECOMMEND 섹션은 Vector DB에서 조회한 관련도가 높은 추천 결과입니다.
                RECOMMEND와 CONTEXT를 바탕으로 사용자의 질문에 맞는 장소를 순서대로 추천해주세요.
                
                CONTEXT : %s
                
                RECOMMEND : %s
                
                반환 형식은 아래와 같습니다
                - 장소 
                - 주소
                - 장소 설명
                - 반려 동물 관련 정보
                - 추천 이유
                위와 같은 형태로 추천도가 높은 순서대로 응답해주세요.
                """.formatted(prompt, recommend);

        // Gemini API로
        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s".formatted(apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(new GeminiRequestDTO(List.of(new GeminiRequestDTO.Content(List.of(new GeminiRequestDTO.Part(finalPrompt))))))))
                .build();
        log.info("프롬프트 : " + finalPrompt);

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        GeminiResponseDTO resp = objectMapper.readValue(response.body(), GeminiResponseDTO.class);
        return resp.candidates().get(0).content().parts().get(0).text();
    }
}

