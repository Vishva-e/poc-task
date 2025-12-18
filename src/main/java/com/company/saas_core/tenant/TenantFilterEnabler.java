package com.company.saas_core.tenant;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * Helper that enables the Hibernate tenant filter for the current session.
 * Services should call {@link #enableFilter()} at the start of transactional methods.
 */
@Component
public class TenantFilterEnabler {

    private final EntityManagerFactory emf;

    public TenantFilterEnabler(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void enableFilter() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) return;

        SessionFactory sf = emf.unwrap(SessionFactory.class);
        Session session = sf.getCurrentSession();
        session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
    }
}
