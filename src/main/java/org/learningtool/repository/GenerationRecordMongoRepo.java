package org.learningtool.repository;

import org.learningtool.model.GenerationRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenerationRecordMongoRepo extends MongoRepository<GenerationRecord, String> {
}
