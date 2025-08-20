package org.learningtool.service;

import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;

public interface GenerateService {
    GenerateResponse generate(GenerateRequest request);
}
