package io.github.petty.pipeline.service;

public interface PromptAssemblyService {
    String assemblePrompt(String visionReport, String location) throws Exception;
}
