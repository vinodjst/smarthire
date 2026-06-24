package com.job.smarthire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  //@configuration - @EnableAutoConfiguration - @ComponentScan - (singleton)
public class SmartHireApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHireApplication.class, args);
	}

}
