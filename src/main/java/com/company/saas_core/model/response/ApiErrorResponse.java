package com.company.saas_core.model.response;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(String code, String message, Map<String, String> details, String traceId,
		Instant timestamp) {
}
