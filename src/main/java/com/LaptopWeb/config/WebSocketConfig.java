package com.LaptopWeb.config;

import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.security.CustomerDecoder;
import com.amazonaws.services.drs.model.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private CustomerDecoder customerDecoder;

    @Autowired
    JwtAuthenticationConverter jwtAuthenticationConverter;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/notifications");
        registry.setUserDestinationPrefix("/notifications");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000/").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                //Authenticate user on CONNECT
                if (nonNull(accessor) && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    //Extract JWT token from header, validate it and extract user authorities
                    var authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (isNull(authHeader) || !authHeader.startsWith("Bearer" + " ")) {

                        // If there is no token present then we should interrupt handshake process and throw an
                        throw new AccessDeniedException(ErrorApp.UNAUTHENTICATED.getMessage());
                    }
                    var token = authHeader.substring("Bearer".length() + 1);
                    Jwt jwt;
                    try {

                        //Validate JWT token with any resource server
                        jwt = customerDecoder.decode(token);
                    } catch (JwtException ex) {

                        //In case the JWT token is expired or cannot be decoded, an AccessDeniedException should bethrown
                        log.warn(ex.getMessage());
                        throw new AccessDeniedException(ErrorApp.UNAUTHENTICATED.getMessage());
                    }
                    JwtAuthenticationToken authentication = (JwtAuthenticationToken) jwtAuthenticationConverter.convert(jwt);
                    accessor.setUser(authentication);
                }

                // Authenticate user on SUBSCRIBE
                if(nonNull(accessor) && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String des = accessor.getDestination();

                    JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) accessor.getUser();

                    if(!hasPermission(des, jwtAuthenticationToken)) {
                        throw new AccessDeniedException("Unauthorized access to: " + des);
                    }
                }


                return message;
            }

        });

    }

    private boolean hasPermission(String des, JwtAuthenticationToken jwtAuthenticationToken) {
        if (jwtAuthenticationToken == null) {
            throw new AccessDeniedException(ErrorApp.UNAUTHENTICATED.getMessage());
        }

        if(des.equals("/admin")) {
            return jwtAuthenticationToken.getAuthorities().stream().anyMatch(auth -> auth
                    .getAuthority().equals("ROLE_ADMIN")
            );
        }

        if(des.contains("/notifications/user")) {
            String username = des.substring("/notification/user/".length());

            boolean checkRole = jwtAuthenticationToken.getAuthorities().stream().anyMatch(auth -> auth
                    .getAuthority().equals("ROLE_USER")
            );

            boolean checkUsername = username.equals(
                    jwtAuthenticationToken.getToken().getClaimAsMap("data").get("username")
            );

            return checkRole && checkUsername;
        }
        return false;
    }
}

