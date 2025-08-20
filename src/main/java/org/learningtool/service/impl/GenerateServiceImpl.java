package org.learningtool.service.impl;

import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;
import org.learningtool.providers.ContentProvider;
import org.learningtool.providers.ProviderFactory;
import org.learningtool.repository.GenerationHistoryRepository;
import org.learningtool.service.GenerateService;
import org.springframework.stereotype.Service;

@Service
public class GenerateServiceImpl implements GenerateService {

    private final ProviderFactory providerFactory;
    private final GenerationHistoryRepository historyRepository;

    public GenerateServiceImpl(ProviderFactory providerFactory, GenerationHistoryRepository historyRepository) {
        this.providerFactory = providerFactory;
        this.historyRepository = historyRepository;
    }

    @Override
    public GenerateResponse generate(GenerateRequest request) {
        ContentProvider provider = providerFactory.getProvider(request.getType());
        GenerateResponse response = provider.generate(request);
        // persist to history (in-memory impl by default)
        historyRepository.save(response);
        return response;
    }
}
