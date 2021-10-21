package com.github.velinyordanov.foodorder.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.velinyordanov.foodorder.data.entities.Customer;
import com.github.velinyordanov.foodorder.services.AuthenticationService;

@ExtendWith(MockitoExtension.class)
public class JwtRequestFilterTest {
	@Mock
	private AuthenticationService authenticationService;
	
	@InjectMocks
	private JwtRequestFilter jwtRequestFilter;
	
	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"Basic", "Digest"})
	public void doFilterInternalShould_notAuthenticateUser_whenAuthorizationHeaderIsMissingOrNotBearer(String authorizationHeader) throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		given(request.getHeader("Authorization")).willReturn(authorizationHeader);
		
		this.jwtRequestFilter.doFilter(request, response, chain);
		
		then(this.authenticationService).shouldHaveNoInteractions();
		
		then(chain).should(times(1)).doFilter(request, response);
		then(chain).shouldHaveNoMoreInteractions();
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
	
	@Test
	public void doFilterInternalShould_notAuthenticateUser_whenTokenIsNotValid() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		given(request.getHeader("Authorization")).willReturn("Bearer jwtToken");
		given(this.authenticationService.getAuthenticationToken("jwtToken")).willReturn(Optional.empty());
		
		this.jwtRequestFilter.doFilter(request, response, chain);
		
		then(this.authenticationService).shouldHaveNoMoreInteractions();
		
		then(chain).should(times(1)).doFilter(request, response);
		then(chain).shouldHaveNoMoreInteractions();
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
	
	@Test
	public void doFilterInternalShould_authenticateUser_whenTokenIsValid() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		
		given(request.getHeader("Authorization")).willReturn("Bearer jwtToken");
		
		Customer customer = new Customer();
		customer.setId("customerId");
		customer.setEmail("custoemrEmail");
		customer.setPassword("customerPassword");
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(customer, customer.getPassword());
		given(this.authenticationService.getAuthenticationToken("jwtToken")).willReturn(Optional.of(usernamePasswordAuthenticationToken));
		
		this.jwtRequestFilter.doFilter(request, response, chain);
		
		then(this.authenticationService).shouldHaveNoMoreInteractions();
		
		then(chain).should(times(1)).doFilter(request, response);
		then(chain).shouldHaveNoMoreInteractions();
		assertEquals(usernamePasswordAuthenticationToken, SecurityContextHolder.getContext().getAuthentication());
	}
}
