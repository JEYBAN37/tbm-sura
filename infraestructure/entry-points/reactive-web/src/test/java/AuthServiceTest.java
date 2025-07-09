
import com.sura.tbm.JwtLogin;
import com.sura.web.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock // Mock de la dependencia JwtLogin
    private JwtLogin jwtLogin;

    @InjectMocks // Inyecta los mocks en la instancia de AuthService
    private AuthService authService;

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_TOKEN = "mockedJwtToken";

    @BeforeEach
    void setUp() {
        // No hay configuraciones globales adicionales por ahora,
        // cada prueba configurará el comportamiento de jwtLogin.allowedPass()
        // según sea necesario.
    }

    @Test
    @DisplayName("Debería retornar un token cuando las credenciales son válidas")
    void login_shouldReturnToken_whenCredentialsAreValid() {
        // GIVEN: jwtLogin.allowedPass() retorna un Mono con el token de prueba
        when(jwtLogin.allowedPass(TEST_USERNAME, TEST_PASSWORD)).thenReturn(Mono.just(TEST_TOKEN));

        // WHEN: Se llama al método login del servicio
        Mono<String> result = authService.login(TEST_USERNAME, TEST_PASSWORD);

        // THEN: Verificar que el Mono emite el token esperado
        StepVerifier.create(result)
                .expectNext(TEST_TOKEN)
                .verifyComplete();

        // Verificar que jwtLogin.allowedPass() fue llamado exactamente una vez con las credenciales correctas
        verify(jwtLogin, times(1)).allowedPass(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    @DisplayName("Debería retornar un Mono vacío cuando las credenciales son inválidas")
    void login_shouldReturnEmptyMono_whenCredentialsAreInvalid() {
        // GIVEN: jwtLogin.allowedPass() retorna un Mono vacío (simulando autenticación fallida)
        when(jwtLogin.allowedPass(TEST_USERNAME, TEST_PASSWORD)).thenReturn(Mono.empty());

        // WHEN: Se llama al método login del servicio
        Mono<String> result = authService.login(TEST_USERNAME, TEST_PASSWORD);

        // THEN: Verificar que el Mono se completa sin emitir ningún valor
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        // Verificar que jwtLogin.allowedPass() fue llamado exactamente una vez con las credenciales correctas
        verify(jwtLogin, times(1)).allowedPass(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    @DisplayName("Debería propagar cualquier error de jwtLogin.allowedPass()")
    void login_shouldPropagateError_whenJwtLoginThrowsError() {
        // GIVEN: jwtLogin.allowedPass() retorna un Mono con un error
        RuntimeException expectedError = new RuntimeException("Error en la autenticación");
        when(jwtLogin.allowedPass(TEST_USERNAME, TEST_PASSWORD)).thenReturn(Mono.error(expectedError));

        // WHEN: Se llama al método login del servicio
        Mono<String> result = authService.login(TEST_USERNAME, TEST_PASSWORD);

        // THEN: Verificar que el Mono emite el error esperado
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error en la autenticación"))
                .verify();

        // Verificar que jwtLogin.allowedPass() fue llamado exactamente una vez con las credenciales correctas
        verify(jwtLogin, times(1)).allowedPass(TEST_USERNAME, TEST_PASSWORD);
    }
}