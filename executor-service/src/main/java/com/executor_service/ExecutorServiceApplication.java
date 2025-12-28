package com.executor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class ExecutorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExecutorServiceApplication.class, args);
	}

}
