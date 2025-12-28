package com.job_service.utils;

import java.util.UUID;

public class JobIdGenerator {

    public static String generate() {
        return "job_" + UUID.randomUUID();
    }
}
