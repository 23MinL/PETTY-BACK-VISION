package io.github.petty.pipeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.petty.pipeline.model.dto.GeminiRequestDTO;
import io.github.petty.pipeline.model.dto.GeminiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Override
    public String forRAG(String prompt) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClient.newHttpClient();

        String url = "%s?key=%s".formatted(apiUrl, apiKey);

        GeminiRequestDTO requestBody = new GeminiRequestDTO(
                List.of(new GeminiRequestDTO.Content(
                        List.of(new GeminiRequestDTO.Part(prompt))
                ))
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        GeminiResponseDTO resp = mapper.readValue(response.body(), GeminiResponseDTO.class);
        return resp.candidates().get(0).content().parts().get(0).text();
    }
}

