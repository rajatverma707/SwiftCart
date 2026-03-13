package com.rv.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RetailServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetailServiceRegistryApplication.class, args);
	}

}
