package com.wenglam.baking_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BakingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BakingAppApplication.class, args);
	}

}
