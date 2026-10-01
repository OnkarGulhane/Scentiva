package com.scentiva.modules.ai.service;

import com.scentiva.modules.ai.dto.SemanticSearchRequest;
import com.scentiva.modules.ai.dto.SemanticSearchResponse;

public interface SemanticSearchService {

    SemanticSearchResponse search(SemanticSearchRequest request);
}
