

import com.sura.jwt.JwtUtil;
import com.sura.jwt.JwtWebFilter;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic; // Import this!
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Import this!
import org.springframework.security.core.context.ReactiveSecurityContextHolder; // Import this!
import org.springframework.security.core.context.SecurityContext; // Import this!
import org.springframework.security.core.context.SecurityContextImpl; // Import this!
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.util.Collections; // Import this!

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtWebFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtWebFilter jwtWebFilter;

    @Mock
    private ServerWebExchange exchange;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private WebFilterChain chain;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.jwt.token";
    private static final String USERNAME = "testuser";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    @BeforeEach
    void setUp() {
        when(exchange.getRequest()).thenReturn(request);
    }

    @Test
    @DisplayName("Debe permitir el paso sin autenticar si no hay cabecera Authorization")
    void filter_noAuthHeader_shouldContinueChain() {
        // GIVEN: La cabecera Authorization es nula
        when(request.getHeaders()).thenReturn(new HttpHeaders());

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // WHEN
        Mono<Void> result = jwtWebFilter.filter(exchange, chain);

        // THEN
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("Debe permitir el paso sin autenticar si la cabecera Authorization no es Bearer")
    void filter_authHeaderNotBearer_shouldContinueChain() {
        // GIVEN: La cabecera Authorization existe pero no empieza con "Bearer "
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic some_token");
        when(request.getHeaders()).thenReturn(headers);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // WHEN
        Mono<Void> result = jwtWebFilter.filter(exchange, chain);

        // THEN
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }


    @Test
    @DisplayName("Debe permitir el paso sin autenticar si el token es inválido")
    void filter_invalidToken_shouldContinueChainWithoutAuthentication() {
        // GIVEN: Una cabecera Authorization con un token inválido
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, AUTH_HEADER_PREFIX + INVALID_TOKEN);
        when(request.getHeaders()).thenReturn(headers);

        // Mockear el comportamiento de JwtUtil (token inválido)
        when(jwtUtil.validateToken(INVALID_TOKEN)).thenReturn(false);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // WHEN
        Mono<Void> result = jwtWebFilter.filter(exchange, chain);

        // THEN
        StepVerifier.create(result)
                .expectNoAccessibleContext()
                .verifyComplete();

        verify(jwtUtil, times(1)).validateToken(INVALID_TOKEN);
        verify(jwtUtil, never()).extractUsername(anyString());
        verify(chain, times(1)).filter(exchange);
    }


}