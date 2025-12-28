package com.user_service.user_service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "secrets")
public class SecretsConfiguration {

    private Datasource datasource;   // rename db → datasource to match secrets.datasource.*

    @Data
    public static class Datasource {
        private String username;
        private String password;
        private String driverClassName; // matches driver-class-name
    }
}


