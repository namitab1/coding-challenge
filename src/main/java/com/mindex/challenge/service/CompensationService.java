package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {

    Compensation readCompensation(String employeeId);
    Compensation createCompensation (String employeeId, Compensation compensation);
}
