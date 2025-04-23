package io.github.petty.vision.adapter.out;

import io.github.petty.vision.config.VisionProperties;
import io.github.petty.vision.dto.gemini.GeminiRequest;
import io.github.petty.vision.dto.gemini.GeminiResponse;
import io.github.petty.vision.port.out.GeminiPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GeminiClientImpl implements GeminiPort {

    private final RestTemplate rt;
    private final VisionProperties prop;

    @Override
    public GeminiResponse generate(GeminiRequest req) {
        // 1. 필수 설정 검증
        validateGeminiConfig();

        // 2. URL 조립 (key 쿼리파라미터)
        String url = UriComponentsBuilder
                .fromHttpUrl(prop.getGemini().getUrl())
                .queryParam("key", prop.getGemini().getKey())
                .toUriString();

        try {
            ResponseEntity<GeminiResponse> res = rt.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(req, jsonHeaders()),
                    GeminiResponse.class
            );

            log.info("Gemini status={} bodyPresent={}", res.getStatusCode(), res.hasBody());

            if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
                throw new IllegalStateException("Gemini 응답 오류 status=" + res.getStatusCode());
            }
            return res.getBody();

        } catch (RestClientException ex) {
            log.error("Gemini 호출 실패", ex);
            throw new IllegalStateException("Gemini 호출 실패", ex);
        }
    }

    /* ────────── private helpers ────────── */

    private void validateGeminiConfig() {
        if (isBlank(prop.getGemini().getUrl()) || isBlank(prop.getGemini().getKey())) {
            throw new IllegalStateException("vision.gemini.url / key 설정이 없습니다");
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private boolean isBlank(String v) { return v == null || v.isBlank(); }
}
