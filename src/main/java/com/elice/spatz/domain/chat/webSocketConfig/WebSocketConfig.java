//package com.elice.spatz.domain.chat.webSocketConfig;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*");
////                .withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/app");
//        registry.enableSimpleBroker("/topic", "/queue");
//    }
//
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//                    accessor.setUser(auth);
//                }
//                return message;
//            }
//        });
//    }
//}


package com.elice.spatz.domain.chat.webSocketConfig;

import com.elice.spatz.domain.user.service.TokenProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenProvider tokenProvider;

    public WebSocketConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        if (request instanceof ServletServerHttpRequest) {
                            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                            String token = servletRequest.getServletRequest().getParameter("token");
                            if (token != null) {
                                try {
                                    Map<String, Object> claims = tokenProvider.getPayloadFromJWTToken(token);
                                    System.out.println("Decoded claims: " + claims);  // 디코딩된 클레임 확인용 로그

                                    // userId와 nickname이 null이 아닌지 확인
                                    if (claims.get("userId") != null && claims.get("nickname") != null) {
                                        attributes.put("userId", claims.get("userId").toString());
                                        attributes.put("username", claims.get("nickname").toString());
                                        System.out.println("User info saved in session: " + attributes);
                                        return true;
                                    } else {
                                        System.out.println("userId or nickname is null in the token payload");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Error decoding token: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("No valid token found in request");
                            }
                        }
                        return false;  // 토큰이 유효하지 않거나 처리 중 오류가 발생한 경우 연결을 거부
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
                        // 핸드셰이크 후 처리가 필요한 경우 여기에 작성
                    }
                });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/queue");
    }
}