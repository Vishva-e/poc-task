package com.company.saas_core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.saas_core.model.UserAccount;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByIdAndTenantId(Long id, Long tenantId);

	List<UserAccount> findAllByTenantId(Long tenantId);

	boolean existsByUsernameAndTenantId(String username, Long tenantId);

	boolean existsByEmailAndTenantId(String email, Long tenantId);

	UserAccount findByUsername(String username);
}
