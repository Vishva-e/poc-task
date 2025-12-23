package com.company.saas_core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.saas_core.api.response.ApiResponse;
import com.company.saas_core.model.request.CreateProductRequest;
import com.company.saas_core.model.response.ProductResponse;
import com.company.saas_core.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER','ROLE_EMPLOYEE')")
	public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", productService.get(id)));
	}

	@GetMapping
<<<<<<< HEAD
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER','ROLE_EMPLOYEE')")
=======
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
>>>>>>> 324e072 (changes commit)
	public ResponseEntity<ApiResponse<List<ProductResponse>>> list() {
		return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", productService.list()));
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Product created successfully", productService.create(request)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
	}

}
