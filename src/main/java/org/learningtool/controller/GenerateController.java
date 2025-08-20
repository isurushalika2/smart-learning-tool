package org.learningtool.controller;

import jakarta.validation.Valid;
import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;
import org.learningtool.service.GenerateService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generate")
@Validated
public class GenerateController {

    private final GenerateService service;

    public GenerateController(GenerateService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GenerateResponse generate(@Valid @RequestBody GenerateRequest request) {
        return service.generate(request);
    }
}
