package io.github.petty.llm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 지역 필터링
 */
@Service
@RequiredArgsConstructor
public class RegionFilterRetrievalService {
    private final VectorStoreService vectorStoreService;
    private final EmbeddingModel embeddingModel;

    public List<Document> retrieve(String prompt, String location) {
        // 사용자 입력 임베딩
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(prompt));

//         location 후보군 생성 (정확 비교만)
        List<String> candidates = List.of(
                location,
                location + "시",
                location + "도",
                location + "군",
                location + "구",
                location + "광역시",
                location + "특별자치도"
        );

        // 문자열로 변환
        String valueList = candidates.stream()
                .map(val -> "\"" + val + "\"")
                .collect(Collectors.joining(", "));

        // 메타 데이터 서치
        String filterEx = String.format("""
        metadata.region in [%s] || metadata.city in [%s]
    """, valueList, valueList);


        // 벡터 검색
        return vectorStoreService.findSimilarWithFilter(prompt, 5, filterEx);
//        return vectorStoreService.findSimilarContents(prompt, 10);
    }
}
