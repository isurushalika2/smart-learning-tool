package org.learningtool.repository;

import org.learningtool.dto.GenerateResponse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(GenerationHistoryRepository.class)
public class InMemoryGenerationHistoryRepository implements GenerationHistoryRepository {

    private final List<GenerateResponse> store = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void save(GenerateResponse response) {
        if (response != null) {
            store.add(response);
        }
    }

    @Override
    public List<GenerateResponse> findAll() {
        synchronized (store) {
            return new ArrayList<>(store);
        }
    }
}
