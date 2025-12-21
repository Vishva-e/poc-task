package com.company.saas_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.saas_core.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
