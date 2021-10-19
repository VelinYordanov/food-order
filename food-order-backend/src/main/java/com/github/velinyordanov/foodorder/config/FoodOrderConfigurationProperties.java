package com.github.velinyordanov.foodorder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FoodOrderConfigurationProperties {
	@Value("${jwt.secret}")
	private String secret;
	
	public String getSecret() {
		return this.secret;
	}
}
