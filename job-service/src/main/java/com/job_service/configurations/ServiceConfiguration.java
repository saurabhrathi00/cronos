package com.job_service.configurations;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "configs")
@Data
public class ServiceConfiguration {

    private JobDb jobDb;
    @Data
    public static class JobDb {
        private String name;
        private String url;
    }
}

