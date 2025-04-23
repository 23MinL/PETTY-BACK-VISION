package io.github.petty.vision.adapter.out;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.petty.vision.config.VisionProperties;
import io.github.petty.vision.dto.together.TogetherRequest;
import io.github.petty.vision.dto.together.TogetherResponse;
import io.github.petty.vision.port.out.TogetherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TogetherClientImpl implements TogetherPort {

    private final RestTemplate rt;
    private final VisionProperties prop;

    @Override
    public TogetherResponse generate(TogetherRequest req) {
        validateTogetherConfig();

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(prop.getTogether().getKey());

        try {
            ResponseEntity<TogetherResponse> res = rt.exchange(
                    prop.getTogether().getUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(req, headers),
                    TogetherResponse.class
            );

            log.info("Together status={} bodyPresent={}", res.getStatusCode(), res.hasBody());

            if (res.getStatusCode() == HttpStatus.OK && res.hasBody()) {
                JsonNode choices = res.getBody().raw().path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    return res.getBody();
                }
                log.error("Together 응답에 choices 가 비어 있습니다: {}", res.getBody().raw());
            }
            throw new IllegalStateException("Together API 오류 status=" + res.getStatusCode());

        } catch (RestClientException ex) {
            log.error("Together 호출 실패", ex);
            throw new IllegalStateException("Together 호출 실패", ex);
        }
    }

    /* ────────── helpers ────────── */

    private void validateTogetherConfig() {
        if (isBlank(prop.getTogether().getUrl()) || isBlank(prop.getTogether().getKey())) {
            throw new IllegalStateException("vision.together.url / key 설정이 없습니다");
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    private boolean isBlank(String v) { return v == null || v.isBlank(); }
}
