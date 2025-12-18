## saas-core — Quick Orientation for AI coding agents

This project is a Spring Boot (Java 17) multi-tenant SaaS backend plus a minimal Angular frontend. Use this file to shortcut onboarding and keep code changes consistent with project patterns.

- **Big picture:** single Spring Boot application exposing JSON REST endpoints (src/main/java/.../controller). Security uses JWTs containing tenantId and roles; tenant isolation is implemented using a column-based approach (tenant_id) + Hibernate Filters (no manual WHERE clauses).

- **Where to look first:**
  - Authentication: `src/main/java/com/company/saas_core/controller/AuthController.java` and `service/UserService.java`
  - JWT handling: `src/main/java/com/company/saas_core/security/JwtService.java` and `JwtAuthenticationFilter.java` (sets `TenantContext` per request)
  - Tenant thread-local: `src/main/java/com/company/saas_core/tenant/TenantContext.java`
  - Tenant filter enabler: `src/main/java/com/company/saas_core/tenant/TenantFilterEnabler.java` (call inside transactional service methods)
  - Filtered entities: e.g. `src/main/java/com/company/saas_core/model/Employee.java` (annotated with `@Filter`/`@FilterDef`)
  - DB migrations: `src/main/resources/db/migration/V1__init.sql` (Flyway)
  - App config: `src/main/resources/application.properties` (DB, JWT secret, OSIV disabled)

- **Multi-tenancy rules (please follow):**
  - Tenant_id is stored in every multi-tenant table as `tenant_id` column (see `BaseEntity`).
  - Use Hibernate Filter (`@Filter`) to restrict result sets. **Do not** add manual `WHERE tenant_id = ?` clauses in repositories or JPQL.
  - Service methods that access tenant-scoped repositories must enable the filter at the start of a transactional method: call `tenantFilterEnabler.enableFilter()` (see `EmployeeService.list()` as example).
  - Tenant is obtained from the authenticated JWT (claim `tenantId`) — the JWT filter sets `TenantContext.setTenantId(...)` for the request.

- **Authentication / JWT conventions:**
  - Login endpoint: POST `/api/auth/login` with JSON { username, password, tenantId }.
  - Successful login returns a signed JWT that contains `tenantId` and `roles` claims.
  - Tokens are expected in `Authorization: Bearer <token>` header.

- **Development & run commands:**
  - Backend build: `mvn -B verify` (task `verify` is available in workspace tasks)
  - Run locally: `mvn spring-boot:run` or use your IDE run configuration
  - DB migrations: Flyway runs automatically on startup (migration scripts in `src/main/resources/db/migration`)
  - Frontend (minimal scaffold): `cd frontend && npm install && npm start`

- **Patterns and conventions:**
  - Layered architecture: `controller` -> `service` -> `repository` (use `@Transactional` on service methods)
  - DTOs are lean; prefer validated request bodies (`@Valid`) in controllers
  - Passwords stored using BCrypt (see `DataInitializer` for example)
  - Exceptions: throw `IllegalArgumentException` for simple validation errors and rely on `RestExceptionHandler` for consistent API responses

- **When adding new tenant-scoped entity:**
  1. Add entity extending `BaseEntity` with `@FilterDef` and `@Filter(name="tenantFilter", condition="tenant_id = :tenantId")`
  2. Add repository interface extending `JpaRepository`
  3. Add service methods and call `tenantFilterEnabler.enableFilter()` at the start of transactional methods before querying/saving
  4. Add a Flyway migration SQL under `src/main/resources/db/migration` to create the table and add `tenant_id` column

- **Testing & CI notes:**
  - Use integration tests that populate a tenant and authenticate to get a token; authenticated requests exercise the full stack (filter + security).
  - Keep tests focused on one tenant per test to ensure isolation semantics.

If anything here is unclear or any pattern is missing from the codebase, tell me which area to expand and I will update this file with concrete examples and code pointers. ✅
