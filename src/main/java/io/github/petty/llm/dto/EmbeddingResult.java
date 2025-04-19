package io.github.petty.llm.dto;

import java.util.List;

// 임베딩 결과
public record EmbeddingResult(String id, String contentId, String text, List<Float> vector) {}

