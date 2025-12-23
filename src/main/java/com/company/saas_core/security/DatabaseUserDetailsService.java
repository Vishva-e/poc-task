package com.company.saas_core.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.company.saas_core.model.UserAccount;
import com.company.saas_core.repository.UserRepository;
import com.company.saas_core.tenant.TenantContext;

@Service
public class DatabaseUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Long tenantId = TenantContext.getTenantId();
		if (tenantId == null) {
			throw new UsernameNotFoundException("Tenant context not set");
		}

		UserAccount user = (userRepository.findByUsername(username));

		return User.builder().username(user.getUsername()).password(user.getPassword()) // already encoded
				.disabled(!user.isEnabled())
				.authorities(Arrays.stream(user.getRoles().split(",")).map(r ->  r).toArray(String[]::new))
				.build();
	}
	
}
