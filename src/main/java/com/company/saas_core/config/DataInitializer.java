package com.company.saas_core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.company.saas_core.model.UserAccount;
import com.company.saas_core.repository.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		UserAccount admin = new UserAccount();
		admin.setId(1l);
		admin.setUsername("superadmin");
		admin.setEmail("superadmin@gmail.com".toLowerCase());
		admin.setPassword(passwordEncoder.encode("superadmin"));
		admin.setRoles("ROLE_SUPER_ADMIN,ROLE_USER,ROLE_EMPLOYEE");
		admin.setEnabled(true);
		admin.setTenantId(0l);
		userRepository.save(admin);

		/**
		 * if (tenantRepository.count() == 0) { Tenant t = new Tenant(); t.setName("Acme
		 * Corp"); t.setApiKey(null); t = tenantRepository.save(t);
		 * System.out.println(t.getId()); UserAccount admin = new UserAccount();
		 * admin.setUsername("admin"); admin.setEmail("admin@gmail.com");
		 * admin.setPassword(passwordEncoder.encode("admin"));
		 * admin.setRoles("ROLE_ADMIN,ROLE_USER,ROLE_EMPLOYEE"); admin.setEnabled(true);
		 * admin.setTenantId(t.getId()); userRepository.save(admin);
		 * 
		 * 
		 * Tenant t1 = new Tenant(); t1.setName("ABC Corp"); t1.setApiKey("ABC"); t1 =
		 * tenantRepository.save(t1);
		 * 
		 * UserAccount admin1 = new UserAccount(); admin1.setUsername("admin1");
		 * admin1.setEmail("admin1@gmail.com");
		 * admin1.setPassword(passwordEncoder.encode("admin1"));
		 * admin1.setRoles("ROLE_ADMIN,ROLE_USER"); admin1.setTenantId(t1.getId());
		 * admin1.setEnabled(true); userRepository.save(admin1);
		 * 
		 * 
		 * }
		 **/

	}
}
