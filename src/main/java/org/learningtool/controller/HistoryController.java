package org.learningtool.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.learningtool.dto.GenerateResponse;
import org.learningtool.repository.GenerationHistoryRepository;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@Validated
@Tag(name = "History", description = "Endpoints to read/write generation history (DynamoDB)")
public class HistoryController {

    private final GenerationHistoryRepository repository;

    public HistoryController(GenerationHistoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all history items", description = "Fetches all items from the configured repository (DynamoDB when enabled)")
    public List<GenerateResponse> getHistory() {
        return repository.findAll();
    }

    @PostMapping(value = "/sample", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Insert a sample item", description = "Writes a sample GenerateResponse into DynamoDB and returns it")
    public GenerateResponse insertSample() {
        GenerateResponse sample = new GenerateResponse();
        sample.setType("SAMPLE");
        sample.setTopic("DynamoDB connectivity test");
        sample.setSummary("This is a sample record inserted at " + Instant.now());
        sample.setItems(List.of("item-1", "item-2"));
        sample.setPayload(Map.of("source", "sample-endpoint"));
        repository.save(sample);
        return sample;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Insert a custom item", description = "Persists the provided GenerateResponse into DynamoDB and echoes it back")
    public GenerateResponse insertCustom(@Valid @RequestBody GenerateResponse body) {
        repository.save(body);
        return body;
    }
}
