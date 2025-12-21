package com.company.saas_core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.saas_core.api.response.ApiResponse;
import com.company.saas_core.model.Employee;
import com.company.saas_core.model.response.EmployeeResponse;
import com.company.saas_core.service.EmployeeService;
import com.company.saas_core.valid.model.CreateEmployeeRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * List all employees (tenant-scoped)
	 * 
	 * @throws Exception
	 */
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<ApiResponse<List<EmployeeResponse>>> list() throws Exception {

		return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employeeService.list()));
	}

	/**
	 * Get employee by ID
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<ApiResponse<Employee>> get(@PathVariable Long id) {

		Employee employee = employeeService.get(id);

		return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", employee));
	}

	/**
	 * Create new employee + user account
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<Employee>> create(@Valid @RequestBody CreateEmployeeRequest request) {

		Employee created = employeeService.create(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Employee created successfully", created));
	}

	/**
	 * Update employee
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<Employee>> update(@PathVariable Long id, @Valid @RequestBody Employee request) {

		Employee updated = employeeService.update(id, request);

		return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated));
	}

	/**
	 * Delete employee
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

		employeeService.delete(id);

		return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
	}
}
