package io.github.petty.llm.controller;

import io.github.petty.llm.service.RegionFilterRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RegionFilterRetrievalService retrievalService;


    @PostMapping
    public ResponseEntity<String> recommend(@RequestBody Map<String, String> promptMap) {
        // 사용자 입력 기반 프롬프트 구성
        String userPrompt = buildPrompt(promptMap);

        // 위치 필드 추출 (예: 서귀포시, 강릉, 인천 등)
        String location = promptMap.get("location");

        // 지역 필터링 + 벡터 유사도 검색
        List<Document> docs = retrievalService.retrieve(userPrompt, location);

        // 검색 결과 확인 (LLM 연결 전 단계)
        docs.forEach(doc ->
                System.out.println("[검색 결과] " + doc.getMetadata().get("title"))
        );

        // 결과 개수 응답
        return ResponseEntity.ok("총 " + docs.size() + "개의 결과가 검색되었습니다.");
    }

    // 사용자 입력 기반 프롬프트 생성
    private String buildPrompt(Map<String, String> promptMap) {
        return String.format("""
            %s와 함께 여행하려고 해요. %s 조건을 만족하는 곳으로 찾고 있어요.
        """,
                promptMap.get("type"),
                promptMap.get("info"));
    }
}