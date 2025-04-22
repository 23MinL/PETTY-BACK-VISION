package io.github.petty.llm.controller;

import io.github.petty.llm.service.ChatService;
import io.github.petty.llm.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final VectorStoreService vectorStoreService;
    private final ChatService chatService;


    @PostMapping
    public ResponseEntity<String> recommend(@RequestBody Map<String, String> promptMap) {
        // 사용자 입력 기반 프롬프트 구성
        String userPrompt = buildPrompt(promptMap);
        List<Document> docs = vectorStoreService.findSimilarContents(userPrompt, 5);

        // 결과 응답
//        for (int i = 0; i < docs.size(); i++) {
//            String title = String.valueOf(docs.get(i).getMetadata().get("title"));
//            System.out.println(title);
//        }
//        return ResponseEntity.ok("success");

        // Gemini 연결
        String result = null;
        try {
            result = chatService.generateFromPrompt(userPrompt, docs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(result);
    }

    // 사용자 입력 기반 프롬프트 생성
    private String buildPrompt(Map<String, String> promptMap) {
        return String.format("""
            %s와 함께 여행하려고 해요. 주소는 %s 근처, 설명은 %s 만족하는 곳으로 찾고 있어요.
        """,
                promptMap.get("type"),
                promptMap.get("location"),
                promptMap.get("info"));
    }
}