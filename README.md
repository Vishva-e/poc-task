# saas-core — Multi-tenant SaaS Reference

This project is a compact, interview-friendly example of a column-based multi-tenant SaaS application.

## Tech stack
- Backend: Java 17, Spring Boot 3.2.x, Spring Security 6, Hibernate 6, JPA
- Database: MySQL (Flyway migrations provided)
- Frontend: Angular 18 (scaffolded), Tailwind CSS

## Key design points
- Column-based multi-tenancy: every tenant-scoped entity stores `tenant_id` (see `BaseEntity`).
- Tenant isolation is enforced using Hibernate filters (`@FilterDef` + `@Filter`) — **do not** use manual `WHERE tenant_id = ?` predicates in business queries.
- `TenantContext` (a ThreadLocal) stores the current request's tenant id. A request's tenant is established from the authenticated JWT (claim `tenantId`).
- `JwtAuthenticationFilter` extracts the `tenantId` and sets `TenantContext` and `SecurityContext` for the request.
- `TenantFilterEnabler` is a small helper that enables the Hibernate `tenantFilter` for the active session — service methods should call `filterEnabler.enableFilter()` at the start of transactional methods that access tenant-scoped repositories.
- Login flow: POST `/api/auth/login` with `{username, password, tenantId}`. The server authenticates the user within the tenant context and issues a JWT that contains `tenantId` and `roles`.

## Getting started
- Configure `spring.datasource.*` in `src/main/resources/application.properties` for your MySQL instance.
- Run `mvn -B verify` to build and `mvn spring-boot:run` to start.
- The application seeds a sample tenant, user `admin/admin` and an employee (see `DataInitializer`).

## Frontend
A minimal Angular 18 scaffold exists in `frontend/` and includes Tailwind in `package.json`. The frontend should implement a JWT interceptor and Employee CRUD pages.

## Notes & best practices
- OSIV is disabled (`spring.jpa.open-in-view=false`) to avoid lazy-loading surprises — services must load required data inside transactions.
- For production, replace the hard-coded JWT secret with a secure vault value, and add proper monitoring, logging, and resilience.
- Add integration tests that authenticate and assert tenant isolation (an admin user in tenant A must not see tenant B data).

## Files of interest
- `TenantContext.java` — ThreadLocal tenant storage
- `Employee` — example tenant-scoped entity (`@FilterDef` + `@Filter`)
- `JwtAuthenticationFilter.java` — sets `TenantContext` and `SecurityContext` per request
- `TenantFilterEnabler.java` — enables the hibernate tenant filter for the current session

# poc-task
