package io.github.petty.pipeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.petty.pipeline.model.dto.TogetherRequestDTO;
import io.github.petty.pipeline.model.dto.TogetherResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class TogetherServiceImpl implements TogetherService {
    @Value("${together.api.url}")
    private String apiUrl;
    @Value("${together.api.key}")
    private String apiKey;
    @Value("${together.api.model}")
    private String apiModel;

    @Override
    public String answer(String prompt) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(new TogetherRequestDTO(apiModel, List.of(new TogetherRequestDTO.Message("user", prompt))));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s".formatted(apiUrl)))
                .header("Authorization", "Bearer %s".formatted(apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), TogetherResponseDTO.class).choices().get(0).message().content();
        }
        throw new Exception("Together API를 호출하지 못하였습니다.");
    }
}
