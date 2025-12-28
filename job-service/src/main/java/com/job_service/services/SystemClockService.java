package com.job_service.services;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SystemClockService implements ClockService{
    @Override
    public Instant now() {
        return Instant.now();
    }
}
