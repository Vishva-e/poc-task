package com.company.saas_core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.saas_core.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameAndTenantId(String name, Long tenantId);

    Optional<Product> findById(Long id);

	Optional<Product> findByIdAndTenantId(Long id, Long tenantId);

}
