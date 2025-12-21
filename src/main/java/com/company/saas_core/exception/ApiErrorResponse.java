package com.company.saas_core.exception;

public class ApiErrorResponse {
	
	private String errorCode;
	private String message;
	private int status;

	public ApiErrorResponse(String errorCode, String message, int status) {
		this.errorCode = errorCode;
		this.message = message;
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
