package io.github.petty.llm.service;

import io.github.petty.tour.entity.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.*;
import org.springframework.stereotype.Service;
import io.github.petty.llm.dto.EmbeddingResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class VectorStoreService {
    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final EmbeddingModel embeddingModel;

    // 콘텐츠를 벡터 저장소에 저장
    public void saveContents(List<Content> contents) {
        List<Document> documents = contents.stream()
                .map(content -> {
                    EmbeddingResult result = embeddingService.embedContent(content);
                    return embeddingService.toDocument(result, content);
                })
                .collect(Collectors.toList());
        
        // Qdrant Vectorstore에 문서 추가
        vectorStore.add(documents);
    }

    // 유사한 콘텐츠 검색 (SearchRequest 기반)
    public List<Document> findSimilarContents(String query, int k) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(k)
                .similarityThreshold(0.1)
                .build();

        return vectorStore.similaritySearch(searchRequest);
    }

//     필터 조건을 사용한 유사 콘텐츠 검색
//    public List<Document> findSimilarWithFilter(String query, int k, String filterExpression) {
//        SearchRequest searchRequest = SearchRequest.builder()
//                .query(query)
//                .topK(k)
//                .similarityThreshold(0.1)
//                .filterExpression(filterExpression)
//                .build();
//
//        List<Document> results = vectorStore.similaritySearch(searchRequest);
//
//        return results;
//    }

    // 저장된 벡터 삭제
    public void deleteByIds(List<String> ids) {
        vectorStore.delete(ids);
    }
}