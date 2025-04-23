package io.github.petty.vision.dto.gemini;

import java.util.List;
import java.util.Map;

public record GeminiRequest(List<Map<String,Object>> contents) {}