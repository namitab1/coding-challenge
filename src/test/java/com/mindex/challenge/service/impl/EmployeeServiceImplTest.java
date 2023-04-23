package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeReportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeReportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reporting-structure";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testCreateGetReportingStructure() {
        Employee testDirectReport1 = new Employee();
        testDirectReport1.setFirstName("John");
        testDirectReport1.setLastName("Doe");
        testDirectReport1.setDepartment("Engineering");
        testDirectReport1.setPosition("Developer");

        Employee testManager = new Employee(); 
        testManager.setFirstName("Jim");
        testManager.setLastName("D");
        testManager.setDepartment("Engineering");
        testManager.setPosition("Manager");
        List<Employee> directReports = new ArrayList<> (Arrays.asList(testDirectReport1));
        testManager.setDirectReports(directReports);
        

        // Create checks
        Employee createdtestDirectReport1 = restTemplate.postForEntity(employeeUrl, testDirectReport1, Employee.class).getBody();

        assertNotNull(createdtestDirectReport1.getEmployeeId());
        assertEmployeeEquivalence(testDirectReport1, createdtestDirectReport1);
        
        Employee createdTestManager = restTemplate.postForEntity(employeeUrl, testManager, Employee.class).getBody();

        assertNotNull(createdTestManager.getEmployeeId());
        assertEmployeeEquivalence(testManager, createdTestManager);


        // Read reporting structure checks
        ReportingStructure reportingStructureDirectReport1 = restTemplate.getForEntity(employeeReportingStructureUrl, ReportingStructure.class, createdtestDirectReport1.getEmployeeId()).getBody();
        assertEquals(0, reportingStructureDirectReport1.getNumberOfReports());
        
        ReportingStructure reportingStructureManager = restTemplate.getForEntity(employeeReportingStructureUrl, ReportingStructure.class, createdTestManager.getEmployeeId()).getBody();
        assertEquals(1, reportingStructureManager.getNumberOfReports());

    }

    
    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
