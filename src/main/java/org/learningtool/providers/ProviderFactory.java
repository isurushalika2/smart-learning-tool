package org.learningtool.providers;

import org.learningtool.dto.GenerateRequest;
import org.springframework.stereotype.Component;

@Component
public class ProviderFactory {
    private final ProviderOpenAi openAi;
    private final ProviderMock mock;

    public ProviderFactory(ProviderOpenAi openAi, ProviderMock mock) {
        this.openAi = openAi;
        this.mock = mock;
    }

    public ContentProvider getProvider(GenerateRequest.ContentType type) {
        // For demo purposes, route everything via OpenAI if API key exists; otherwise use mock provider.
        if (openAi.isConfigured()) {
            return openAi;
        }
        return mock;
    }
}
