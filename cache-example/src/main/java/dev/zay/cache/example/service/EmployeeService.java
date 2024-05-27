package dev.zay.cache.example.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import dev.zay.cache.example.model.Employee;
import dev.zay.cache.example.repository.EmployeeRepository;
import jakarta.transaction.Transactional;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Cacheable(value = "employee")
	public List<Employee> getEmployees() {
		System.out.println("Get the employee list");
		return employeeRepository.findAll();
	}

	@Cacheable(value = "employee", key = "#id")
	public Employee getEmployeeById(int id) {
		System.out.println("Get the employee with id : " + id);
		return employeeRepository.findById(id).orElse(null);
	}

	@CachePut(value = "employee", key = "#employee.id")
	public Employee updateEmployee(Employee employee) {
		System.out.println("Update the employee with id : " + employee.getId());
		Employee updatedEmployee = employeeRepository.save(employee);
		return updatedEmployee;
	}

	@Transactional
	@CacheEvict(value = "employee", allEntries = true)
	public void deleteEmployee(int id) {
		System.out.println("Delete the employee with id : " + id);
		employeeRepository.deleteById(id);
	}
	
	@CacheEvict(value = "employee", allEntries = true)
	public Employee saveEmployee(Employee employee) {
		System.out.println("Save the employee");
		return employeeRepository.save(employee);
	}
}
