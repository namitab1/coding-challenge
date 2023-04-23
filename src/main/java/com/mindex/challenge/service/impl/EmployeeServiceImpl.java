package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Read employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

	@Override
	public ReportingStructure getReportingStructure(String employeeId) {
        LOG.debug("Get reporting structure for employye with Id[{}]", employeeId);
        
        ReportingStructure reportingStructure = new ReportingStructure();
        Employee employee = read(employeeId);
        reportingStructure.setEmployee(employee);
        int numberOfReports = 0 ;
		if (employee.getDirectReports() != null) {
			List<Employee> uniqueDirectReports = employee.getDirectReports().stream().distinct()
					.collect(Collectors.toList());
			numberOfReports = uniqueDirectReports.size();
		}
        reportingStructure.setNumberOfReports(numberOfReports);
		return reportingStructure;
	}
}
