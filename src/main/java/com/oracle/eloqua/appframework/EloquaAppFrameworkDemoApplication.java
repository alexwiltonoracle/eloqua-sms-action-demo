package com.oracle.eloqua.appframework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EloquaAppFrameworkDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EloquaAppFrameworkDemoApplication.class, args);
	}
}
