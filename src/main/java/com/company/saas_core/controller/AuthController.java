package com.company.saas_core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.saas_core.api.response.ApiResponse;
import com.company.saas_core.model.request.LoginRequest;
import com.company.saas_core.model.response.LoginResponse;
import com.company.saas_core.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest r) throws Exception {

		return ResponseEntity
				.ok(ApiResponse.success("Login successful", userService.authenticate(r.username, r.password)));

	}
}
