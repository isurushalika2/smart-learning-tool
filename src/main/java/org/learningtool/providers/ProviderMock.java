package org.learningtool.providers;

import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProviderMock implements ContentProvider {
    @Override
    public GenerateResponse generate(GenerateRequest request) {
        GenerateResponse resp = new GenerateResponse();
        resp.setType(request.getType().name());
        resp.setTopic(request.getTopic());
        resp.setSummary("Mock content for " + request.getTopic() + " (" + request.getType().name() + ")");
        List<String> items = new ArrayList<>();
        Map<String, Object> payload = new LinkedHashMap<>();

        switch (request.getType()) {
            case IMAGE -> {
                items.add("An illustrative image concept about: " + request.getTopic());
                payload.put("imageUrls", List.of("https://placehold.co/600x400?text=" + urlSafe(request.getTopic())));
            }
            case ANIMATION -> {
                items.add("A simple animation idea demonstrating: " + request.getTopic());
                payload.put("animationStoryboard", List.of("Frame 1: Intro", "Frame 2: Core idea", "Frame 3: Example", "Frame 4: Summary"));
            }
            case SHORT_NOTES -> {
                items.addAll(List.of(
                        "Definition and concept",
                        "Key points",
                        "Simple example",
                        "Common pitfalls",
                        "Quick references"
                ));
            }
            case CHEAT_SHEET -> {
                items.addAll(List.of("Syntax", "Common operations", "Best practices", "Shortcuts"));
                payload.put("cheatSheet", Map.of(
                        "Syntax", "...",
                        "Examples", List.of("Example 1", "Example 2")
                ));
            }
            case LEETCODE -> {
                items.add("Practice problems related to: " + request.getTopic());
                payload.put("problems", List.of(
                        Map.of("id", 1, "title", "Two Sum (Variant)", "difficulty", "Easy"),
                        Map.of("id", 2, "title", "Valid Parentheses (Variant)", "difficulty", "Easy")
                ));
                payload.put("explanations", List.of("High-level explanation", "Step-by-step reasoning"));
            }
            case VOICE_EXPLANATION -> {
                items.add("Voice explanation script generated for: " + request.getTopic());
                payload.put("ssml", "<speak>Here is a beginner-friendly explanation of " + escapeXml(request.getTopic()) + "</speak>");
                payload.put("audioBase64", ""); // Leave empty in mock
            }
            case INTERVIEW_QA -> {
                items.addAll(List.of("5 interview questions", "Model answers", "Tips"));
                payload.put("qa", List.of(
                        Map.of("q", "What is " + request.getTopic() + "?", "a", "Beginner-friendly answer"),
                        Map.of("q", "Explain core concept.", "a", "Clear explanation")
                ));
            }
        }

        resp.setItems(items);
        resp.setPayload(payload);
        return resp;
    }

    private String urlSafe(String s) {
        return s.replace(" ", "+");
    }

    private String escapeXml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
