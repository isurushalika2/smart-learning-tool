package org.learningtool.providers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learningtool.dto.GenerateRequest;

import static org.junit.jupiter.api.Assertions.*;

class ProviderFactoryTest {

    @Test
    @DisplayName("Returns OpenAI provider when configured")
    void returnsOpenAiWhenConfigured() {
        ProviderOpenAi openAi = new ProviderOpenAi("sk-xyz", "https://api.openai.com");
        ProviderMock mock = new ProviderMock();
        ProviderFactory factory = new ProviderFactory(openAi, mock);

        ContentProvider p = factory.getProvider(GenerateRequest.ContentType.SHORT_NOTES);
        assertTrue(p instanceof ProviderOpenAi);
    }

    @Test
    @DisplayName("Returns Mock provider when OpenAI not configured")
    void returnsMockWhenNotConfigured() {
        ProviderOpenAi openAi = new ProviderOpenAi("", "https://api.openai.com");
        ProviderMock mock = new ProviderMock();
        ProviderFactory factory = new ProviderFactory(openAi, mock);

        ContentProvider p = factory.getProvider(GenerateRequest.ContentType.SHORT_NOTES);
        assertTrue(p instanceof ProviderMock);
    }
}
