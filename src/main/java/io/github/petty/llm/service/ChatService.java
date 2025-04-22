package io.github.petty.llm.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface ChatService {
    String generateFromPrompt(String prompt, List<Document> docs) throws Exception;
}
