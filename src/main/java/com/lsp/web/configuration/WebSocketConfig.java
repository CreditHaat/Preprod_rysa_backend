package com.lsp.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // clients subscribe here
        config.setApplicationDestinationPrefixes("/app"); // clients send messages here (optional)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // WebSocket endpoint
                .setAllowedOriginPatterns("*") // replace * with specific domains in production
                .withSockJS();
//    	registry.addEndpoint("/ws")
//        .setAllowedOriginPatterns("*")
//        .setSessionCookieNeeded(false)  // ➕ add this line
//        .withSockJS();
    }
    
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*")
//                .withSockJS()
//                .setSessionCookieNeeded(false); // ✅ disable cookie requirement
//    }

}
