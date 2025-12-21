package com.company.saas_core.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.saas_core.model.Tenant;
import com.company.saas_core.model.UserAccount;
import com.company.saas_core.model.request.CreateTenantRequest;
import com.company.saas_core.repository.TenantRepository;
import com.company.saas_core.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantService {

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public Tenant createTenant(CreateTenantRequest request) {

		if (tenantRepository.existsByNameIgnoreCase(request.getTenantName())) {
			throw new IllegalArgumentException("Tenant key already exists");
		}

		Tenant tenant = new Tenant();
		tenant.setName(request.getTenantName());
		tenant.setApiKey(UUID.randomUUID().toString());
		tenant = tenantRepository.save(tenant);

		UserAccount admin = new UserAccount();
		admin.setUsername(request.getAdminUsername());
		admin.setEmail(request.getAdminEmail().toLowerCase());
		admin.setPassword(passwordEncoder.encode(request.getAdminPassword()));
		admin.setRoles("ROLE_ADMIN");
		admin.setEnabled(true);
		admin.setTenantId(tenant.getId());

		if (userRepository.existsByUsernameAndTenantId(admin.getUsername(), tenant.getId())) {
			throw new IllegalStateException("Username already exists");
		}

		if (userRepository.existsByEmailAndTenantId(admin.getEmail(), tenant.getId())) {
			throw new IllegalStateException("Email already exists");
		}
		
		userRepository.save(admin);
		return tenant;
	}

	@Transactional
	public List<Tenant> listTenants() {
		return tenantRepository.findAll();
	}
}
