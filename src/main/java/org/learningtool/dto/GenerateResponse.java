package org.learningtool.dto;

import java.util.List;
import java.util.Map;

public class GenerateResponse {
    private String type;
    private String topic;
    private String summary;
    private List<String> items; // notes bullets, interview Q/A summaries, etc.
    private Map<String, Object> payload; // provider-specific payload e.g. image URLs, code, audio, etc.

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
