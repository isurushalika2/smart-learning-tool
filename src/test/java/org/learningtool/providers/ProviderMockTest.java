package org.learningtool.providers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProviderMockTest {

    @Test
    @DisplayName("IMAGE type returns imageUrls in payload")
    void imageType_hasImageUrls() {
        ProviderMock mock = new ProviderMock();
        GenerateRequest req = new GenerateRequest();
        req.setType(GenerateRequest.ContentType.IMAGE);
        req.setTopic("Java Basics");
        GenerateResponse resp = mock.generate(req);

        assertEquals("IMAGE", resp.getType());
        assertEquals("Java Basics", resp.getTopic());
        assertNotNull(resp.getPayload());
        Object urls = resp.getPayload().get("imageUrls");
        assertNotNull(urls);
        assertTrue(urls instanceof List);
        assertFalse(((List<?>) urls).isEmpty());
    }

    @Test
    @DisplayName("SHORT_NOTES contains items list")
    void shortNotes_hasItems() {
        ProviderMock mock = new ProviderMock();
        GenerateRequest req = new GenerateRequest();
        req.setType(GenerateRequest.ContentType.SHORT_NOTES);
        req.setTopic("Collections");
        GenerateResponse resp = mock.generate(req);

        assertEquals("SHORT_NOTES", resp.getType());
        assertNotNull(resp.getItems());
        assertFalse(resp.getItems().isEmpty());
    }

    @Test
    @DisplayName("INTERVIEW_QA has qa payload entries")
    void interview_hasQa() {
        ProviderMock mock = new ProviderMock();
        GenerateRequest req = new GenerateRequest();
        req.setType(GenerateRequest.ContentType.INTERVIEW_QA);
        req.setTopic("Streams");
        GenerateResponse resp = mock.generate(req);

        Map<String, Object> payload = resp.getPayload();
        assertNotNull(payload);
        Object qa = payload.get("qa");
        assertNotNull(qa);
        assertTrue(qa instanceof List);
        assertFalse(((List<?>) qa).isEmpty());
    }
}
