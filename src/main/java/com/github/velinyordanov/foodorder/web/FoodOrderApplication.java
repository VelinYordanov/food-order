package com.github.velinyordanov.foodorder.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.github.velinyordanov.foodorder.entities")
@EnableJpaRepositories("com.github.velinyordanov.foodorder.data")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class FoodOrderApplication {

    public static void main(String[] args) {
	SpringApplication.run(FoodOrderApplication.class, args);
    }
}
