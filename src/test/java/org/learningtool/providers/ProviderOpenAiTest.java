package org.learningtool.providers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProviderOpenAiTest {

    @Test
    @DisplayName("isConfigured returns false when api key is blank")
    void isConfigured_falseWhenBlank() {
        ProviderOpenAi openAi = new ProviderOpenAi("", "https://api.openai.com");
        assertFalse(openAi.isConfigured());
    }

    @Test
    @DisplayName("isConfigured returns true when api key is present")
    void isConfigured_trueWhenPresent() {
        ProviderOpenAi openAi = new ProviderOpenAi("sk-abc", "https://api.openai.com");
        assertTrue(openAi.isConfigured());
    }
}
