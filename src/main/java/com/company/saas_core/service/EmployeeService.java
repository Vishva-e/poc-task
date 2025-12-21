package com.company.saas_core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.saas_core.exception.ConflictException;
import com.company.saas_core.model.Employee;
import com.company.saas_core.model.UserAccount;
import com.company.saas_core.model.response.EmployeeResponse;
import com.company.saas_core.model.response.UserResponse;
import com.company.saas_core.repository.EmployeeRepository;
import com.company.saas_core.repository.UserRepository;
import com.company.saas_core.tenant.TenantContext;
import com.company.saas_core.tenant.TenantFilterEnabler;
import com.company.saas_core.valid.model.CreateEmployeeRequest;

@Service
public class EmployeeService {

	@Autowired
	private  EmployeeRepository employeeRepository;
	@Autowired
	private  UserRepository userRepository;
	@Autowired
	private  PasswordEncoder passwordEncoder;
	@Autowired
	private  TenantFilterEnabler filterEnabler;

	public EmployeeService(EmployeeRepository employeeRepository, TenantFilterEnabler filterEnabler,
			UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.employeeRepository = employeeRepository;
		this.filterEnabler = filterEnabler;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Employee create(CreateEmployeeRequest req) {
		UserAccount user = new UserAccount();
		Employee employee = new Employee();
		try {

			Long tenantId = TenantContext.getTenantId();
			if (tenantId == null) {
				throw new IllegalStateException("No tenant in context");
			}

			if (userRepository.existsByUsernameAndTenantId(req.getUsername(), tenantId)) {
				throw new ConflictException("Username already exists");
			}

			if (userRepository.existsByEmailAndTenantId(req.getEmail().toLowerCase(), tenantId)) {
				throw new ConflictException("Email already exists");
			}

			user.setUsername(req.getUsername());
			user.setEmail(req.getEmail().toLowerCase());
			user.setPassword(passwordEncoder.encode(req.getPassword()));
			user.setRoles("ROLE_EMPLOYEE");
			user.setTenantId(tenantId);

			userRepository.save(user);

			employee.setFirstName(req.getFirstName());
			employee.setLastName(req.getLastName());
			employee.setPosition("ROLE_EMPLOYEE");
			employee.setEmail(req.getEmail().toLowerCase());
			employee.setContactNumber(req.getContactNumber());
			employee.setUserAccount(user);
			employee.setTenantId(tenantId);

			return employeeRepository.save(employee);
		} catch (Exception e) {
			userRepository.delete(user);
			employeeRepository.delete(employee);
			throw new ConflictException(e.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public List<EmployeeResponse> list() throws Exception {
		filterEnabler.enableFilter();
		List<Employee> employees = new ArrayList<>();
		try {
			employees = employeeRepository.findAll();
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
		return employees.stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public Employee get(Long id) {
		filterEnabler.enableFilter();
		return employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
	}

	@Transactional
	public Employee update(Long id, Employee updated) {
		filterEnabler.enableFilter();
		Employee existing = employeeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Not found"));
		existing.setFirstName(updated.getFirstName());
		existing.setLastName(updated.getLastName());
		existing.setEmail(updated.getEmail());
		existing.setPosition(updated.getPosition());
		return employeeRepository.save(existing);
	}

	@Transactional
	public void delete(Long id) {
		filterEnabler.enableFilter();
		employeeRepository.deleteById(id);
	}

	private EmployeeResponse mapToResponse(Employee employee) {
		UserAccount user = employee.getUserAccount();

		return new EmployeeResponse(employee.getId(), employee.getFirstName(), employee.getLastName(),
				employee.getEmail(), employee.getPosition(), new UserResponse(user.getId(), user.getUsername(),
						user.getEmail(), Arrays.asList(user.getRoles().split(",")), user.isEnabled()),
				user.isEnabled());
	}
}
