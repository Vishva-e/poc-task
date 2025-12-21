package com.company.saas_core.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.saas_core.model.UserAccount;
import com.company.saas_core.model.request.UserRequest;
import com.company.saas_core.model.response.LoginResponse;
import com.company.saas_core.model.response.UserResponse;
import com.company.saas_core.repository.UserRepository;
import com.company.saas_core.security.JwtService;
import com.company.saas_core.tenant.TenantContext;
import com.company.saas_core.tenant.TenantFilterEnabler;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private TenantFilterEnabler filterEnabler;

	@Value("${app.security.jwt.expiration}")
	private long jwtExpirationMs;

	@Transactional(readOnly = true)
	public LoginResponse authenticate(String username, String password) throws Exception {
		try {

			UserAccount user = userRepository.findByUsername(username);
			if (!passwordEncoder.matches(password, user.getPassword())) {
				throw new IllegalArgumentException("Invalid credentials");
			}
			Long tenantId = user.getTenantId();
			TenantContext.setTenantId(tenantId);
			filterEnabler.enableFilter();
			String[] roles = user.getRoles() == null ? new String[0] : user.getRoles().split(",");
			
			LoginResponse response = new LoginResponse();
			response.setAccessToken(jwtService.generateToken(user.getUsername(), tenantId, Arrays.asList(roles)));
			response.setTokenType("Bearer");
			response.setExpiresIn(jwtExpirationMs);
			response.setUsername(user.getUsername());
			response.setRoles(Arrays.asList(roles));
			return response;
		} catch (Exception ex) {
			throw new Exception("Invalid credentials");
		} finally {
			TenantContext.clear();
		}
	}

	public UserResponse create(UserRequest request) {
		Long tenantId = requireTenant();
		if (userRepository.existsByUsernameAndTenantId(request.getUsername(), tenantId))
			throw new IllegalStateException("Username already exists");

		if (userRepository.existsByEmailAndTenantId(request.getEmail(), tenantId))
			throw new IllegalStateException("Email already exists");

		UserAccount user = new UserAccount();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(String.join(",", request.getRoles()));
		user.setEnabled(true);
		user.setTenantId(tenantId);
		return map(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public List<UserResponse> list() {
		Long tenantId = requireTenant();
		return userRepository.findAllByTenantId(tenantId).stream().map(this::map).toList();
	}

	public UserResponse update(Long id, UserRequest request) {
		Long tenantId = requireTenant();

		UserAccount user = userRepository.findByIdAndTenantId(id, tenantId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setRoles(String.join(",", request.getRoles()));

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		return map(user);
	}

	public void delete(Long id) {
		Long tenantId = requireTenant();
		userRepository.delete(userRepository.findByIdAndTenantId(id, tenantId)
				.orElseThrow(() -> new RuntimeException("User not found")));
	}

	private Long requireTenant() {
		Long tenantId = TenantContext.getTenantId();
		if (tenantId == null)
			throw new IllegalStateException("Tenant not set");
		return tenantId;
	}

	private UserResponse map(UserAccount user) {
		return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), List.of(user.getRoles().split(",")),
				user.isEnabled());
	}

}
