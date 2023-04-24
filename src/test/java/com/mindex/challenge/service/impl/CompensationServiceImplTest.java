package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

	private String employeeUrl;
	private String employeeCompensationUrl;
	
	private Float salary = (float) 10000.00;
	
	private Employee createdEmployee;

    @LocalServerPort
    private int port;
 
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";

        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        
    }

    @Test
    public void testCreateCompensation() throws ParseException {

    	Compensation compensation = new Compensation();
    	compensation.setEmployee(createdEmployee);
    	compensation.setSalary(salary);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String dateInString = "2023-05-01";
    	Date date = sdf.parse(dateInString);
    	
    	compensation.setEffectiveDate(date);
    	
    	   employeeCompensationUrl =  "http://localhost:" + port + "/employee/"+ createdEmployee.getEmployeeId()
    			   +"/compensation";
    	Compensation createdCompensation = restTemplate.postForEntity(employeeCompensationUrl, compensation, Compensation.class).getBody();

        assertNotNull(createdCompensation.getId());
        assertNotNull(createdCompensation.getEmployee());
        assertEquals(createdEmployee.getEmployeeId(), createdCompensation.getEmployee().getEmployeeId());
        assertNotNull(createdCompensation.getEffectiveDate());
        assertEquals(compensation.getEffectiveDate(), createdCompensation.getEffectiveDate());
        assertNotNull(createdCompensation.getSalary());
        
    }
}
