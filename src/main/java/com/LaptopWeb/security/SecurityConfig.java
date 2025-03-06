package com.LaptopWeb.security;



import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.exception.ErrorApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
            "/auth/login/**",
            "/auth/logout",
            "/auth/refresh",
            "/auth/introspect",
            "/payment/vn-pay/**",
            "/email/**",
            "/payment/zalo-pay/call-back",
            "/auth/outbound/**",
    };

    private CustomerDecoder customerDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/ws/**").permitAll()

                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/brands/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reviews/{reviewId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reviews/byRating/**").permitAll()

                        .requestMatchers("/brands/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/v1/auth/login")
                        .defaultSuccessUrl("/auth/loginSuccess", true)
                        .failureUrl("/auth/loginFailure")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login/local")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(customizer -> customizer
                        .accessDeniedHandler(customAccessDeniedHanlder())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customerDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );

        return http.build();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHanlder() {
        return (request, response, accessDeniedException) -> {
            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(false)
                    .message(ErrorApp.ACCESS_DENIED.getMessage())
                    .build();

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        };
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        
        return jwtAuthenticationConverter;
    }
}
