package com.github.velinyordanov.foodorder.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import com.github.velinyordanov.foodorder.data.entities.BaseUser;

@Configuration
public class WebsocketSecurityConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(
	    MessageSecurityMetadataSourceRegistry messages) {
	messages
		.simpDestMatchers("/notifications/restaurants/{restaurantId}/orders")
		.access("hasAuthority('ROLE_RESTAURANT') and @websocketSecurityConfiguration.verifyId(principal, message)")
		.simpDestMatchers("/notifications/customers/{customerId}/orders/**")
		.access("hasAuthority('ROLE_CUSTOMER') and @websocketSecurityConfiguration.verifyId(principal, message)")
		.simpTypeMatchers(SimpMessageType.DISCONNECT, SimpMessageType.UNSUBSCRIBE)
		.permitAll()
		.anyMessage()
		.authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
	return true;
    }

    public boolean verifyId(BaseUser principal, Message<?> message) {
	StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
	String topic = sha.getDestination();
	String[] destinationPath = topic.split("/");
	if (destinationPath.length < 3) {
	    return false;
	}

	String id = destinationPath[3];

	if (!id.equals(principal.getId())) {
	    return false;
	}

	return true;
    }
}
