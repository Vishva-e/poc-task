package com.company.saas_core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.saas_core.model.Product;
import com.company.saas_core.model.request.CreateProductRequest;
import com.company.saas_core.model.request.UpdateProductRequest;
import com.company.saas_core.model.response.ProductResponse;
import com.company.saas_core.repository.ProductRepository;
import com.company.saas_core.tenant.TenantContext;

@Service
public class ProductService {

	@Autowired
	ProductRepository productRepository;

	@Transactional
	public ProductResponse create(CreateProductRequest request) {

		Long tenantId = TenantContext.getTenantId();
		if (tenantId == null) {
			throw new IllegalStateException("Tenant not found");
		}

		if (productRepository.existsByNameAndTenantId(request.getName(), tenantId)) {
			throw new IllegalArgumentException("Product already exists");
		}

		Product product = new Product();
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());
		product.setTenantId(tenantId);
		Product saved = productRepository.save(product);

		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> list() {
		return productRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public ProductResponse get(Long id) {
		Long tenantId = TenantContext.getTenantId();
		Product p = productRepository.findByIdAndTenantId(id, tenantId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		return mapToResponse(p);
	}

	@Transactional
	public ProductResponse update(Long id, UpdateProductRequest req) {
		Long tenantId = TenantContext.getTenantId();
		Product p = productRepository.findByIdAndTenantId(id, tenantId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		p.setName(req.getName());
		p.setDescription(req.getDescription());
		p.setPrice(req.getPrice());
		p.setStock(req.getStock());
		return mapToResponse(p);
	}

	@Transactional
	public void delete(Long id) {
		Long tenantId = TenantContext.getTenantId();
		Product p = productRepository.findByIdAndTenantId(id, tenantId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		productRepository.delete(p);
	}

	private ProductResponse mapToResponse(Product p) {
		ProductResponse res = new ProductResponse();
		res.setId(p.getId());
		res.setName(p.getName());
		res.setPrice(p.getPrice());
		res.setDescription(p.getDescription());
		res.setStock(p.getStock());
		return res;
	}
}
