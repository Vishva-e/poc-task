package com.company.saas_core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.saas_core.api.response.ApiResponse;
import com.company.saas_core.model.Tenant;
import com.company.saas_core.model.request.CreateTenantRequest;
import com.company.saas_core.service.TenantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/tenants")
@RequiredArgsConstructor
public class TenantController {

	@Autowired
	private TenantService tenantService;

	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
	public ResponseEntity<ApiResponse<Tenant>> createTenant(@Valid @RequestBody CreateTenantRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Tenent created successfully", tenantService.createTenant(request)));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
	public ResponseEntity<ApiResponse<List<Tenant>>> getAllTenants() {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Tenents retrieved successfully", tenantService.listTenants()));
	}
}
