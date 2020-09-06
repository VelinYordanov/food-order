package com.github.velinyordanov.foodorder.config;

import java.util.Collection;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class FoodOrderConfiguration {
    private final Collection<AbstractConverter> converters;

    public FoodOrderConfiguration(Collection<AbstractConverter> converters) {
	this.converters = converters;
    }

    @Bean
    public ModelMapper modelMapper() {
	ModelMapper mapper = new ModelMapper();
	this.converters.forEach(mapper::addConverter);
	return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
    }
}
