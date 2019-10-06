package com.github.velinyordanov.foodorder.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EntityScan("com.github.velinyordanov.foodorder.entities")
public class FoodOrderApplication {

    public static void main(String[] args) {
	SpringApplication.run(FoodOrderApplication.class, args);
    }
}
