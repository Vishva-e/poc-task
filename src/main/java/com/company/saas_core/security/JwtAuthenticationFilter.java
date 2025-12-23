package com.company.saas_core.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.company.saas_core.tenant.TenantContext;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			extractAndAuthenticate(request);
			filterChain.doFilter(request, response);
		} finally {
			TenantContext.clear();
		}
	}

	private void extractAndAuthenticate(HttpServletRequest request) {
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
			return;
		}
		String token = authHeader.substring(BEARER_PREFIX.length());

		Claims claims = jwtService.parseClaims(token);

		String username = claims.getSubject();
		Long tenantId = extractTenantId(claims);
		List<SimpleGrantedAuthority> authorities = extractAuthorities(claims);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
				authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		TenantContext.setTenantId(tenantId);
	}

	private Long extractTenantId(Claims claims) {
		Object tenant = claims.get("tenantId");

		if (tenant instanceof Number number) {
			return number.longValue();
		}

		if (tenant instanceof String text && StringUtils.hasText(text)) {
			try {
				return Long.parseLong(text);
			} catch (NumberFormatException ignored) {
				return null;
			}
		}

		return null;
	}

	private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
		List<String> roles = claims.get("roles", List.class);

		if (roles == null || roles.isEmpty()) {
			return List.of();
		}

		return roles.stream().filter(StringUtils::hasText).map(SimpleGrantedAuthority::new).toList();
	}
}
