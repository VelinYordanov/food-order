package com.github.velinyordanov.foodorder.websockets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class WebsocketErrorHandler extends StompSubProtocolErrorHandler {
	private static final Log LOGGER = LogFactory.getLog(WebsocketErrorHandler.class);

	@Override
	public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
		LOGGER.error("A websocket error occurred", ex);

		StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
		if (ex instanceof AccessDeniedException || ex.getCause() instanceof AccessDeniedException) {
			accessor.setMessage("Credentials not valid.");
		} else {
			accessor.setMessage("An error occurred. Try again later.");
		}

		accessor.setLeaveMutable(true);

		StompHeaderAccessor clientHeaderAccessor = null;
		if (clientMessage != null) {
			clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
			if (clientHeaderAccessor != null) {
				String receiptId = clientHeaderAccessor.getReceipt();
				if (receiptId != null) {
					accessor.setReceiptId(receiptId);
				}
			}
		}

		return handleInternal(accessor, new byte[0], ex, clientHeaderAccessor);
	}

}
