package com.sura.tbm.defaults;

import com.sura.jwt.JwtWebFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableReactiveMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtWebFilter jwtWebFilter;
    private final AppSecurityProperties properties;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        if (properties.getCors() != null) {
            http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        }


        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .pathMatchers(properties.getPublicResources().toArray(new String[0])).permitAll()
                .anyExchange().authenticated()
        );

        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        if (properties.isAuthEnable()) {
            http.addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(properties.getCors().isAllowCredentials());
        corsConfig.setAllowedOrigins(properties.getCors().getAllowedOrigins());
        corsConfig.setAllowedMethods(properties.getCors().getAllowedMethods());
        corsConfig.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}
