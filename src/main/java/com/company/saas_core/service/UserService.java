package com.company.saas_core.service;

import com.company.saas_core.security.JwtService;
import com.company.saas_core.model.UserAccount;
import com.company.saas_core.repository.UserRepository;
import com.company.saas_core.tenant.TenantContext;
import com.company.saas_core.tenant.TenantFilterEnabler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantFilterEnabler filterEnabler;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TenantFilterEnabler filterEnabler) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.filterEnabler = filterEnabler;
    }

    @Transactional(readOnly = true)
    public String authenticate(String username, String password, Long tenantId) {
        try {
            // For login we set the tenant in context and enable the hibernate filter so repositories are scoped
            TenantContext.setTenantId(tenantId);
            filterEnabler.enableFilter();

            UserAccount user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("Invalid credentials");
            }

            String[] roles = user.getRoles() == null ? new String[0] : user.getRoles().split(",");
            return jwtService.generateToken(user.getUsername(), tenantId, Arrays.asList(roles));
        } finally {
            TenantContext.clear();
        }
    }
}
