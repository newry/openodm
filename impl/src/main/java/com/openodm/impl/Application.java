package com.openodm.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * App
 *
 */
@SpringBootApplication(scanBasePackages = "com.openodm")
@EnableAutoConfiguration
@Configuration
@EntityScan(basePackages = { "com.openodm" })
@EnableJpaRepositories(basePackages = { "com.openodm" })
@EnableTransactionManagement
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}