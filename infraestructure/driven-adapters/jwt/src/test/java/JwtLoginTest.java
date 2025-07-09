import com.sura.jwt.JwtLogin;
import com.sura.jwt.JwtUtil;
import com.sura.jwt.TokenDto;
import com.sura.model.common.ex.BusinessException; // Ajusta el paquete
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class JwtLoginTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks // Inyecta los mocks en la instancia de JwtLogin
    private JwtLogin jwtLogin;

    private final String CORRECT_USR = "usuario";
    private final String CORRECT_PASSWORD = "password";
    private final String DUMMY_TOKEN = "mocked_jwt_token_123";


    @Test
    @DisplayName("Debe permitir el acceso y generar un token para credenciales correctas")
    void allowedPass_shouldReturnTokenForCorrectCredentials() {
        // GIVEN: Configura el comportamiento del mock de JwtUtil
        // Cuando se llame a generateToken con cualquier String, devuelve nuestro token de prueba
        when(jwtUtil.generateToken(anyString())).thenReturn(DUMMY_TOKEN);

        // WHEN: Llama al método que estamos probando con credenciales correctas
        Mono<TokenDto> resultMono = jwtLogin.allowedPass(CORRECT_USR, CORRECT_PASSWORD);

        // THEN: Verifica el resultado utilizando StepVeifier para el Mono
        StepVerifier.create(resultMono)
                .expectNext( TokenDto.builder().Token(DUMMY_TOKEN).build()) // Espera que el Mono emita el token esperado
                .verifyComplete(); // Verifica que el Mono se haya completado exitosamente

        // Verifica que el método generateToken de jwtUtil fue llamado exactamente una vez
        // con el usuario correcto
        verify(jwtUtil, times(1)).generateToken(CORRECT_USR);
    }

    @Test
    @DisplayName("Debe retornar un error para credenciales incorrectas (usuario incorrecto)")
    void allowedPass_shouldReturnErrorForIncorrectUser() {
        // GIVEN: Credenciales incorrectas
        String incorrectUser = "otro_usuario";
        String correctPassword = CORRECT_PASSWORD;

        // WHEN: Llama al método que estamos probando con un usuario incorrecto
        Mono<TokenDto> resultMono = jwtLogin.allowedPass(incorrectUser, correctPassword);

        // THEN: Verifica que el Mono emita un error de tipo BusinessException
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException)
                .verify(); // Verifica que se haya producido un error

        // Verifica que el método generateToken de jwtUtil NO fue llamado
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Debe retornar un error para credenciales incorrectas (contraseña incorrecta)")
    void allowedPass_shouldReturnErrorForIncorrectPassword() {
        // GIVEN: Credenciales incorrectas
        String correctUser = CORRECT_USR;
        String incorrectPassword = "otra_password";

        // WHEN: Llama al método que estamos probando con una contraseña incorrecta
        Mono<TokenDto> resultMono = jwtLogin.allowedPass(correctUser, incorrectPassword);

        // THEN: Verifica que el Mono emita un error de tipo BusinessException
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException)
                .verify(); // Verifica que se haya producido un error

        // Verifica que el método generateToken de jwtUtil NO fue llamado
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Debe retornar un error para credenciales incorrectas (ambos incorrectos)")
    void allowedPass_shouldReturnErrorForBothIncorrect() {
        // GIVEN: Credenciales incorrectas
        String incorrectUser = "wrong_user";
        String incorrectPassword = "wrong_password";

        // WHEN: Llama al método que estamos probando con ambos incorrectos
        Mono<TokenDto> resultMono = jwtLogin.allowedPass(incorrectUser, incorrectPassword);

        // THEN: Verifica que el Mono emita un error de tipo BusinessException
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException)
                .verify(); // Verifica que se haya producido un error

        // Verifica que el método generateToken de jwtUtil NO fue llamado
        verify(jwtUtil, never()).generateToken(anyString());
    }
}