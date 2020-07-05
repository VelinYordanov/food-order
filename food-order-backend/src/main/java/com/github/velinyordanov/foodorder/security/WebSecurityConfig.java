package com.github.velinyordanov.foodorder.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final RestaurantAuthenticationProvider restaurantAuthenticationProvider;
    private final CustomerAuthenticationProvider customerAuthenticationProvider;

    public WebSecurityConfig(
	    RestaurantAuthenticationProvider restaurantAuthenticationProvider,
	    CustomerAuthenticationProvider customerAuthenticationProvider) {
	super();
	this.restaurantAuthenticationProvider = restaurantAuthenticationProvider;
	this.customerAuthenticationProvider = customerAuthenticationProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
	return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	auth
		.authenticationProvider(restaurantAuthenticationProvider)
		.authenticationProvider(customerAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
	// We will use method security.
	httpSecurity.csrf()
		.disable()
		.authorizeRequests()
		.anyRequest()
		.permitAll()
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}