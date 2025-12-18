package com.company.saas_core.service;

import com.company.saas_core.model.Employee;
import com.company.saas_core.repository.EmployeeRepository;
import com.company.saas_core.tenant.TenantContext;
import com.company.saas_core.tenant.TenantFilterEnabler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TenantFilterEnabler filterEnabler;

    public EmployeeService(EmployeeRepository employeeRepository, TenantFilterEnabler filterEnabler) {
        this.employeeRepository = employeeRepository;
        this.filterEnabler = filterEnabler;
    }

    @Transactional
    public Employee create(Employee employee) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) throw new IllegalStateException("No tenant in context");
        employee.setTenantId(tenantId);
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> list() {
        // enable tenant filter for the current session so queries are automatically scoped
        filterEnabler.enableFilter();
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee get(Long id) {
        filterEnabler.enableFilter();
        return employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Transactional
    public Employee update(Long id, Employee updated) {
        filterEnabler.enableFilter();
        Employee existing = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
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
}
