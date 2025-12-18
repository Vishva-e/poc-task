package com.company.saas_core.security;

import com.company.saas_core.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter that validates JWT token and sets SecurityContext and TenantContext for the request.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                Claims claims = jwtService.parseClaims(token);
                String username = claims.getSubject();
                // tenant id may be stored as number or string depending on the JWT encoder; support both
                Object tenantObj = claims.get("tenantId");
                Long tenantId = null;
                if (tenantObj instanceof Number) {
                    tenantId = ((Number) tenantObj).longValue();
                } else if (tenantObj instanceof String) {
                    try { tenantId = Long.parseLong((String) tenantObj); } catch (NumberFormatException ignored) {}
                }
                List<String> roles = claims.get("roles", List.class);

                List<SimpleGrantedAuthority> authorities = roles == null ? List.of() : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Set tenant for the duration of the request
                TenantContext.setTenantId(tenantId);
            }
        } catch (Exception ex) {
            // clear any partial state
            SecurityContextHolder.clearContext();
            TenantContext.clear();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // clean up
            TenantContext.clear();
        }
    }
}
