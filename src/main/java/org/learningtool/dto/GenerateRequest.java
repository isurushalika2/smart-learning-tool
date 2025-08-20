package org.learningtool.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GenerateRequest {
    public enum ContentType {
        IMAGE, ANIMATION, SHORT_NOTES, CHEAT_SHEET, LEETCODE, VOICE_EXPLANATION, INTERVIEW_QA
    }

    @NotNull
    private ContentType type;

    @NotBlank
    private String topic; // e.g., "Java Basics", "React Hooks", "Kubernetes"

    private String level = "beginner"; // beginner | intermediate | advanced

    private String language = "en"; // output language

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
