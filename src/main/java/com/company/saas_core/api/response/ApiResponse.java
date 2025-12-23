package com.company.saas_core.api.response;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {

	private final boolean success;
	private final String message;
	private final String errorCode;
	private final T data;
	private final Instant timestamp;
	private final String traceId;

	private ApiResponse(boolean success, String message, String errorCode, T data) {
		this.success = success;
		this.message = message;
		this.errorCode = errorCode;
		this.data = data;
		this.timestamp = Instant.now();
		this.traceId = UUID.randomUUID().toString();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, null, data);
	}

	public static ApiResponse<Void> success(String message) {
		return new ApiResponse<>(true, message, null, null);
	}

	public static ApiResponse<Void> failure(String message, String errorCode) {
		return new ApiResponse<>(false, message, errorCode, null);
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public T getData() {
		return data;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public String getTraceId() {
		return traceId;
	}

}