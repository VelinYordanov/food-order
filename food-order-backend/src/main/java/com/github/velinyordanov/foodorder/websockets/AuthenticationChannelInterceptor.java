package com.github.velinyordanov.foodorder.websockets;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.github.velinyordanov.foodorder.services.AuthenticationService;

@Component
public class AuthenticationChannelInterceptor implements ChannelInterceptor {
	private final AuthenticationService authenticationService;

	public AuthenticationChannelInterceptor(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			final String authorization = accessor.getFirstNativeHeader("Authorization");
			if (authorization != null) {
				String jwtToken = authorization.substring(7);
				this.authenticationService.getAuthenticationToken(jwtToken).ifPresent(accessor::setUser);
			}
		}

		return message;
	}
}
