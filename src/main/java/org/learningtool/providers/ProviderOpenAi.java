package org.learningtool.providers;

import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProviderOpenAi implements ContentProvider {

    private final WebClient client;
    private final String apiKey;
    private final String baseUrl;

    public ProviderOpenAi(
            @Value("${openai.api.key:}") String apiKey,
            @Value("${openai.api.base:https://api.openai.com}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public GenerateResponse generate(GenerateRequest request) {
        // Minimal example: call a chat completions-like endpoint to get text; other types will just build structured payloads
        String prompt = buildPrompt(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", "gpt-4o-mini");
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert tutor that generates concise, beginner-friendly learning assets."),
                Map.of("role", "user", "content", prompt)
        ));

        try {
            Map<?, ?> response = client.post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(ex -> Mono.just(Map.of("error", ex.getMessage())))
                    .block();

            String text = extractText(response);
            GenerateResponse resp = new GenerateResponse();
            resp.setType(request.getType().name());
            resp.setTopic(request.getTopic());
            resp.setSummary(text);

            Map<String, Object> payload = new LinkedHashMap<>();
            if (request.getType() == GenerateRequest.ContentType.IMAGE) {
                payload.put("imageUrls", List.of("https://placehold.co/600x400?text=" + request.getTopic()));
            }
            if (response != null) payload.put("raw", response);
            resp.setPayload(payload);
            return resp;
        } catch (Exception e) {
            GenerateResponse resp = new GenerateResponse();
            resp.setType(request.getType().name());
            resp.setTopic(request.getTopic());
            resp.setSummary("OpenAI call failed: " + e.getMessage());
            return resp;
        }
    }

    private String buildPrompt(GenerateRequest request) {
        return "Generate " + request.getType().name().toLowerCase().replace('_',' ') +
                " for topic '" + request.getTopic() + "' for a " + request.getLevel() + " learner in " + request.getLanguage() + ". " +
                "Include bullet points and keep it concise.";
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> response) {
        if (response == null) return "";
        try {
            var choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) return response.toString();
            var msg = (Map<String, Object>) choices.get(0).get("message");
            if (msg == null) return response.toString();
            return (String) msg.getOrDefault("content", response.toString());
        } catch (Exception e) {
            return response.toString();
        }
    }
}
