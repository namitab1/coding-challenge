package com.mindex.challenge.service.impl; 
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;

@Service
public class CompensationServiceImpl implements CompensationService {
	
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CompensationRepository compensationRepository;
	
	@Override
	public Compensation readCompensation(String employeeId) {
		
		LOG.debug("Read employee compensation with employee Id[{}]", employeeId);
		
		Employee employee = employeeRepository.findByEmployeeId(employeeId);
		
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

		return compensationRepository.findCompensationByEmployee(employee);
	}

	@Override
	public Compensation createCompensation(String employeeId, Compensation compensation) {

		Employee employee = employeeRepository.findByEmployeeId(employeeId);
		
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }
        
        compensation.setEmployee(employee);
        compensation.setId(UUID.randomUUID().toString());
        
        compensationRepository.insert(compensation);
        
        return compensation;
	}

}
