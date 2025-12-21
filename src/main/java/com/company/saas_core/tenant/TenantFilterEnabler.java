package com.company.saas_core.tenant;

import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Helper that enables the Hibernate tenant filter for the current session.
 * Services should call {@link #enableFilter()} at the start of transactional methods.
 */
@Component
public class TenantFilterEnabler {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void enableFilter() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return;
        }

        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter")
               .setParameter("tenantId", tenantId);
    }
}
