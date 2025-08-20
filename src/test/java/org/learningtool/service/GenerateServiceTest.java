package org.learningtool.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;
import org.learningtool.providers.ProviderFactory;
import org.learningtool.providers.ProviderMock;
import org.learningtool.providers.ProviderOpenAi;
import org.learningtool.repository.InMemoryGenerationHistoryRepository;
import org.learningtool.service.impl.GenerateServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class GenerateServiceTest {

    @Test
    @DisplayName("GenerateService delegates to ProviderFactory and returns response")
    void generate_delegates() {
        // OpenAI not configured -> factory should return mock
        ProviderOpenAi openAi = new ProviderOpenAi("", "https://api.openai.com");
        ProviderMock mock = new ProviderMock();
        ProviderFactory factory = new ProviderFactory(openAi, mock);
        InMemoryGenerationHistoryRepository repo = new InMemoryGenerationHistoryRepository();
        GenerateService service = new GenerateServiceImpl(factory, repo);

        GenerateRequest req = new GenerateRequest();
        req.setType(GenerateRequest.ContentType.SHORT_NOTES);
        req.setTopic("Java Streams");

        GenerateResponse resp = service.generate(req);

        assertNotNull(resp);
        assertEquals("SHORT_NOTES", resp.getType());
        assertEquals("Java Streams", resp.getTopic());
        assertNotNull(resp.getSummary());
        assertFalse(repo.findAll().isEmpty());
    }
}
