package com.santa4you.santa_delivery_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class Santa4YouChristmasDeliveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(Santa4YouChristmasDeliveryServiceApplication.class, args);
	}

}
