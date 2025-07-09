import com.sura.model.common.ex.BusinessException;
import com.sura.model.common.ex.FormatException;
import com.sura.model.common.ex.TechnicalException;
import com.sura.web.error.ApiExceptionHandler;
import com.sura.web.error.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler apiExceptionHandler;

    @BeforeEach
    void setUp() {
        apiExceptionHandler = new ApiExceptionHandler();
    }

    @Test
    void handleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");
        Mono<ResponseEntity<ErrorResponse>> response = apiExceptionHandler.handleResponseStatusException(ex);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertNotNull(entity);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(TechnicalException.Type.ERROR_EXCEPCION_RESPUESTA_ESTADO.build().getId(),
                entity.getBody().getErrors().get(0).getId());
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Generic error");
        Mono<ResponseEntity<ErrorResponse>> response = apiExceptionHandler.handleGenericException(ex);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertNotNull(entity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(TechnicalException.Type.ERROR_EXCEPCION_GENERICA.build().getId(),
                entity.getBody().getErrors().get(0).getId());
    }

    @Test
    void handleBusinessExceptions() {
        BusinessException ex = BusinessException.Type.ERROR_CREDEENCIALES_INCORRECTAS.build();
        Mono<ResponseEntity<ErrorResponse>> response = apiExceptionHandler.handleBusinessExceptions(ex);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertNotNull(entity);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals("No se ha podido realizar la operación solicitada", entity.getBody().getErrors().get(0).getMessage());
    }

    @Test
    void handleServerWebInputException() {
        ServerWebInputException ex = new ServerWebInputException("Invalid input");

        Mono<ResponseEntity<ErrorResponse>> response = apiExceptionHandler.handleServerWebInputException(ex);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertNotNull(entity);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(FormatException.Type.ERROR_VALOR_ATRIBUTO_INVALIDO.build().getId(),
                entity.getBody().getErrors().get(0).getId());
    }

    @Test
    void handleServerWebInputException_withWebExchangeBindException() {
        WebExchangeBindException webExchangeBindException = mock(WebExchangeBindException.class);
        when(webExchangeBindException.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(webExchangeBindException.getFieldErrors()).thenReturn(Collections.emptyList());

        Mono<ResponseEntity<ErrorResponse>> response = apiExceptionHandler.handleServerWebInputException(webExchangeBindException);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertNotNull(entity);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(FormatException.Type.ERROR_VALOR_ATRIBUTO_INVALIDO.build().getId(),
                entity.getBody().getErrors().get(0).getId());
    }

    @Test
    void handleServerWebInputException_withWebExchangeBindException_withFieldErrors() {
        WebExchangeBindException webExchangeBindException = mock(WebExchangeBindException.class);
        when(webExchangeBindException.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");
        when(webExchangeBindException.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        Mono<ResponseEntity<ErrorResponse>> response = apiExceptionHandler.handleServerWebInputException(webExchangeBindException);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertNotNull(entity);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(FormatException.Type.ERROR_VALOR_ATRIBUTO_INVALIDO.build().getId(),
                entity.getBody().getErrors().get(0).getId());
    }

    @Test
    void handleServerWebInputException_withWebExchangeBindException_notBadRequest() {
        WebExchangeBindException webExchangeBindException = mock(WebExchangeBindException.class);
        when(webExchangeBindException.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");
        when(webExchangeBindException.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        Mono<ResponseEntity<ErrorResponse>> response = apiExceptionHandler.handleServerWebInputException(webExchangeBindException);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertNotNull(entity);
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(FormatException.Type.ERROR_VALOR_ATRIBUTO_INVALIDO.build().getId(),
                entity.getBody().getErrors().get(0).getId());
    }

}