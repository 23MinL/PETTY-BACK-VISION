package io.github.petty.vision.port.out;

import io.github.petty.vision.dto.gemini.GeminiRequest;
import io.github.petty.vision.dto.gemini.GeminiResponse;

public interface GeminiPort {
    GeminiResponse generate(GeminiRequest req);
}