package com.company.saas_core.model.response;

import java.util.List;

import com.company.saas_core.api.response.BaseResponse;

public record UserResponse(Long id, String username, String email, List<String> roles, boolean enabled)
		implements BaseResponse {
}