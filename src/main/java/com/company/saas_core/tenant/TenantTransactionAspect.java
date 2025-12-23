package com.company.saas_core.tenant;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Aspect
@Component
public class TenantTransactionAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void beforeTransaction() {

        Long tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            return; 
        }

        Session session = entityManager.unwrap(Session.class);

        session.enableFilter("tenantFilter")
               .setParameter("tenantId", tenantId);
    }

    @After("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void afterTransaction() {

        Session session = entityManager.unwrap(Session.class);

        if (session.getEnabledFilter("tenantFilter") != null) {
            session.disableFilter("tenantFilter");
        }
    }
}
