package org.learningtool.repository;

import org.learningtool.dto.GenerateResponse;

import java.util.List;

public interface GenerationHistoryRepository {
    void save(GenerateResponse response);
    List<GenerateResponse> findAll();
}
