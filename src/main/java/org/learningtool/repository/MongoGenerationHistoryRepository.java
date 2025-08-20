package org.learningtool.repository;

import org.learningtool.dto.GenerateResponse;
import org.learningtool.model.GenerationRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
public class MongoGenerationHistoryRepository implements GenerationHistoryRepository {

    private final GenerationRecordMongoRepo mongoRepo;

    public MongoGenerationHistoryRepository(GenerationRecordMongoRepo mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public void save(GenerateResponse response) {
        if (response == null) return;
        GenerationRecord rec = new GenerationRecord();
        rec.setType(response.getType());
        rec.setTopic(response.getTopic());
        rec.setSummary(response.getSummary());
        rec.setItems(response.getItems());
        rec.setPayload(response.getPayload());
        rec.setCreatedAt(Instant.now());
        mongoRepo.save(rec);
    }

    @Override
    public List<GenerateResponse> findAll() {
        return mongoRepo.findAll().stream().map(rec -> {
            GenerateResponse resp = new GenerateResponse();
            resp.setType(rec.getType());
            resp.setTopic(rec.getTopic());
            resp.setSummary(rec.getSummary());
            resp.setItems(rec.getItems());
            resp.setPayload(rec.getPayload());
            return resp;
        }).collect(Collectors.toList());
    }
}
