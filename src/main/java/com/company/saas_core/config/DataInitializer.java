package com.company.saas_core.config;

import com.company.saas_core.model.Employee;
import com.company.saas_core.model.Tenant;
import com.company.saas_core.model.UserAccount;
import com.company.saas_core.repository.EmployeeRepository;
import com.company.saas_core.repository.TenantRepository;
import com.company.saas_core.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(TenantRepository tenantRepository, UserRepository userRepository, EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (tenantRepository.count() == 0) {
            Tenant t = new Tenant();
            t.setName("Acme Corp");
            t.setCode("acme");
            t = tenantRepository.save(t);

            UserAccount admin = new UserAccount();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles("ROLE_ADMIN,ROLE_USER");
            admin.setTenantId(t.getId());
            userRepository.save(admin);

            Employee e = new Employee();
            e.setFirstName("John");
            e.setLastName("Doe");
            e.setEmail("john.doe@acme.com");
            e.setPosition("Engineer");
            e.setTenantId(t.getId());
            employeeRepository.save(e);
        }
    }
}
