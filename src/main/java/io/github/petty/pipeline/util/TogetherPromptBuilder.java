package io.github.petty.pipeline.util;

public interface TogetherPromptBuilder {
    String buildPrompt(String visionReport, String location) throws Exception;
}
