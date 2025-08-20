package org.learningtool.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;
import org.learningtool.service.GenerateService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GenerateController.class)
class GenerateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenerateService generateService;

    @Test
    @DisplayName("POST /api/generate returns 200 with response body when request is valid")
    void generate_ok() throws Exception {
        GenerateRequest req = new GenerateRequest();
        req.setType(GenerateRequest.ContentType.SHORT_NOTES);
        req.setTopic("Java Basics");
        req.setLevel("beginner");
        req.setLanguage("en");

        GenerateResponse resp = new GenerateResponse();
        resp.setType("SHORT_NOTES");
        resp.setTopic("Java Basics");
        resp.setSummary("Some notes");

        Mockito.when(generateService.generate(Mockito.any())).thenReturn(resp);

        mockMvc.perform(post("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is("SHORT_NOTES")))
                .andExpect(jsonPath("$.topic", is("Java Basics")))
                .andExpect(jsonPath("$.summary", is("Some notes")));
    }

    @Test
    @DisplayName("POST /api/generate returns 400 when required fields missing")
    void generate_validationError() throws Exception {
        // missing type and topic
        String badJson = "{}";
        mockMvc.perform(post("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }
}
