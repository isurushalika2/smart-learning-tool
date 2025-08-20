package org.learningtool.providers;

import org.learningtool.dto.GenerateRequest;
import org.learningtool.dto.GenerateResponse;

public interface ContentProvider {
    GenerateResponse generate(GenerateRequest request);
}
