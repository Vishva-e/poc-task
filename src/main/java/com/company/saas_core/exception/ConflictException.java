package com.company.saas_core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

	public ConflictException(String message) {
        super(message);
    }
}