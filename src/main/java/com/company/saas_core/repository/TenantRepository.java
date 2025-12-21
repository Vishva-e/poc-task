package com.company.saas_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.saas_core.model.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

	boolean existsByNameIgnoreCase(String name);

}
