package org.learningtool.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.learningtool.dto.GenerateResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

/**
 * DynamoDB implementation of GenerationHistoryRepository.
 * Enabled when property dynamodb.enabled=true. Stores the full GenerateResponse as JSON under attribute "response".
 */
public class DynamoDbGenerationHistoryRepository implements GenerationHistoryRepository {

    private final DynamoDbClient dynamo;
    private final String tableName;
    private final ObjectMapper mapper;

    public DynamoDbGenerationHistoryRepository(
            ObjectMapper mapper,
            @Value("${dynamodb.table:learningtool-generation-history}") String tableName,
            @Value("${dynamodb.region:}") String region
    ) {
        this.mapper = mapper;
        this.tableName = (tableName == null || tableName.isBlank()) ? "learningtool-generation-history" : tableName;
        String effectiveRegion = (region != null && !region.isBlank())
                ? region
                : (System.getenv("AWS_REGION") != null && !System.getenv("AWS_REGION").isBlank()
                    ? System.getenv("AWS_REGION")
                    : "us-east-1");
        this.dynamo = DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(effectiveRegion))
                .build();
    }

    @Override
    public void save(GenerateResponse response) {
        if (response == null) return;
        try {
            String id = UUID.randomUUID().toString();
            String json = mapper.writeValueAsString(response);
            PutItemRequest req = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(Map.of(
                            "id", AttributeValue.builder().s(id).build(),
                            "createdAt", AttributeValue.builder().s(Instant.now().toString()).build(),
                            "type", AttributeValue.builder().s(nullToEmpty(response.getType())).build(),
                            "topic", AttributeValue.builder().s(nullToEmpty(response.getTopic())).build(),
                            "response", AttributeValue.builder().s(json).build()
                    ))
                    .build();
            dynamo.putItem(req);
        } catch (Exception e) {
            // Swallow to avoid breaking the request flow; in production you may want to log this via a logger
        }
    }

    @Override
    public List<GenerateResponse> findAll() {
        List<GenerateResponse> results = new ArrayList<>();
        try {
            ScanRequest scan = ScanRequest.builder().tableName(tableName).build();
            ScanResponse resp = dynamo.scan(scan);
            if (resp.items() == null) return results;
            for (Map<String, AttributeValue> item : resp.items()) {
                AttributeValue v = item.get("response");
                if (v != null && v.s() != null) {
                    try {
                        GenerateResponse r = mapper.readValue(v.s(), GenerateResponse.class);
                        results.add(r);
                    } catch (Exception ignored) {
                    }
                } else {
                    // Fallback: reconstruct minimal response from separate attributes if present
                    GenerateResponse r = new GenerateResponse();
                    r.setType(avToString(item.get("type")));
                    r.setTopic(avToString(item.get("topic")));
                    results.add(r);
                }
            }
        } catch (Exception e) {
            // ignore and return what we have
        }
        return results;
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static String avToString(AttributeValue v) { return v == null ? null : v.s(); }
}
