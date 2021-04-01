package com.github.velinyordanov.foodorder.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
	private final AuthenticationChannelInterceptor channelInterceptor;
	private final WebsocketErrorHandler websocketErrorHandler;

	public WebsocketConfiguration(AuthenticationChannelInterceptor channelInterceptor,
			WebsocketErrorHandler websocketErrorHandler) {
		this.channelInterceptor = channelInterceptor;
		this.websocketErrorHandler = websocketErrorHandler;
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/notifications");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").withSockJS();
		registry.setErrorHandler(this.websocketErrorHandler);
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(this.channelInterceptor);
	}
}
