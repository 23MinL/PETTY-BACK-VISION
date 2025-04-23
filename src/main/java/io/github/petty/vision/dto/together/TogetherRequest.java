package io.github.petty.vision.dto.together;

import java.util.List;
import java.util.Map;

/**
 * Together Chat Completion 요청용 DTO
 * * 예시
 * {
 *   "model": "meta-llama/Llama-3.2‑11B‑Vision‑Instruct‑Turbo‑Free",
 *   "messages": [ ... ]
 * }
 */
public record TogetherRequest(
        String model,
        List<Map<String, Object>> messages
) {}
