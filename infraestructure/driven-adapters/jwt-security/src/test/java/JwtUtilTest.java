
import com.sura.tbm.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.AfterEach; // Importar AfterEach
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    // Secreto de prueba: debe tener al menos 32 bytes para HS256
    private static final String TEST_SECRET = "estaesunaclavesecretamuyseguraparapruebasjwtutilquelarga";
    // Tiempo de expiración de prueba: 10 minutos en milisegundos
    private static final long TEST_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(10);
    private static final String TEST_USERNAME = "usuario_prueba";

    // Usamos un reloj fijo para pruebas determinísticas
    private Clock fixedClock;
    private JwtUtil jwtUtil;

    @BeforeEach // Se ejecuta antes de cada método de prueba
    void setUp() {
        // Establecer un instante fijo para las pruebas
        // El año 2025-07-08T12:00:00Z es solo un ejemplo, puedes elegir cualquier fecha futura.
        Instant fixedInstant = Instant.parse("2025-07-08T12:00:00Z");
        fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        // Establecer temporalmente la propiedad del sistema para que Date.now() y System.currentTimeMillis()
        // devuelvan este tiempo fijo. Esto es un workaround para JJWT que usa new Date() internamente.
        System.setProperty("fixed.clock.millis", String.valueOf(fixedInstant.toEpochMilli()));

        // Inicializar JwtUtil con el secreto de prueba y la expiración
        jwtUtil = new JwtUtil(TEST_EXPIRATION_MS, TEST_SECRET);
    }

    @AfterEach // Se ejecuta después de cada método de prueba
    void tearDown() {
        // Limpiar la propiedad del sistema después de cada prueba para evitar efectos secundarios
        System.clearProperty("fixed.clock.millis");
    }

    @Test
    @DisplayName("Debería extraer el nombre de usuario de un token válido")
    void extractUsername_shouldExtractCorrectUsername() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    @DisplayName("Debería lanzar JwtException al extraer el nombre de usuario de un token malformado")
    void extractUsername_shouldThrowExceptionForMalformedToken() {
        String malformedToken = "token.malformado.string";
        assertThrows(MalformedJwtException.class, () -> jwtUtil.extractUsername(malformedToken));
    }

    @Test
    @DisplayName("Debería lanzar SignatureException al extraer el nombre de usuario de un token con firma inválida")
    void extractUsername_shouldThrowExceptionForInvalidSignature() {
        // Generar un token con una clave diferente
        Key anotherKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String tokenWithDifferentSignature = Jwts.builder()
                .setSubject(TEST_USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TEST_EXPIRATION_MS))
                .signWith(anotherKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        assertThrows(SignatureException.class, () -> jwtUtil.extractUsername(tokenWithDifferentSignature));
    }

    @Test
    @DisplayName("Debería validar un token válido y no expirado")
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("Debería retornar false para un token expirado")
    void validateToken_shouldReturnFalseForExpiredToken() throws InterruptedException {
        // Generar un token con una expiración muy corta (ej. 1 segundo)
        long shortExpiration = TimeUnit.SECONDS.toMillis(1);
        JwtUtil shortLivedJwtUtil = new JwtUtil(shortExpiration, TEST_SECRET);
        String token = shortLivedJwtUtil.generateToken(TEST_USERNAME);

        // Esperar más allá del tiempo de expiración del token
        Thread.sleep(shortExpiration + 500); // Esperar 1.5 segundos

        // Aquí es importante: si tu JwtUtil internamente usa `System.currentTimeMillis()`
        // para la validación, necesitas "avanzar" el reloj para simular que el tiempo ha pasado.
        // Si tu JwtUtil no usa `System.currentTimeMillis()` o si lo obtuviera de una fuente
        // controlable (como un `Clock` inyectado), esta línea no sería necesaria.
        // Para esta implementación, Jwts.parserBuilder().build().parseClaimsJws(token)
        // internamente compara con `System.currentTimeMillis()`.
        System.setProperty("fixed.clock.millis", String.valueOf(Instant.now().toEpochMilli()));


        assertFalse(jwtUtil.validateToken(token), "El token debería estar expirado y la validación debería fallar");
        // Opcionalmente, puedes verificar que la excepción ExpiredJwtException se lanzaría si no la estuvieras capturando
        assertThrows(ExpiredJwtException.class, () ->
                Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes())).build().parseClaimsJws(token)
        );
    }

    @Test
    @DisplayName("Debería retornar false para un token con firma inválida")
    void validateToken_shouldReturnFalseForInvalidSignature() {
        // Generar un token con una clave diferente
        Key anotherKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        String tokenWithDifferentSignature = Jwts.builder()
                .setSubject(TEST_USERNAME)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TEST_EXPIRATION_MS))
                .signWith(anotherKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtil.validateToken(tokenWithDifferentSignature));
    }

    @Test
    @DisplayName("Debería retornar false para un token malformado")
    void validateToken_shouldReturnFalseForMalformedToken() {
        String malformedToken = "esto.no.es.un.jwt.valido";
        assertFalse(jwtUtil.validateToken(malformedToken));
    }

    @Test
    @DisplayName("Debería retornar false para una cadena de token vacía")
    void validateToken_shouldReturnFalseForEmptyToken() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    @DisplayName("Debería retornar false para un token nulo")
    void validateToken_shouldReturnFalseForNullToken() {
        assertFalse(jwtUtil.validateToken(null));
    }
}