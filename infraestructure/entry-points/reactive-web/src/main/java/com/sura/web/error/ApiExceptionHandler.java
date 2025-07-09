package com.sura.web.error;

import com.sura.model.common.ex.ApplicationException;
import com.sura.model.common.ex.FormatException;
import com.sura.model.common.ex.TechnicalException;
import com.sura.web.error.dto.ErrorDetail;
import com.sura.web.error.dto.ErrorResponse;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.util.Collections;

@ControllerAdvice()
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatusException(ResponseStatusException exception) {
        ErrorDetail errorDetail = toErrorDetail(TechnicalException.Type.ERROR_EXCEPCION_RESPUESTA_ESTADO.build());
        errorDetail = errorDetail.toBuilder()
                .detail(exception.getMessage())
                .build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(errorDetail))
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, exception.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        ErrorDetail errorDetail = toErrorDetail(TechnicalException.Type.ERROR_EXCEPCION_GENERICA.build());
        errorDetail = errorDetail.toBuilder()
                .detail(ex.getMessage())
                .build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(errorDetail))
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ApplicationException.class)
    public final Mono<ResponseEntity<ErrorResponse>> handleBusinessExceptions(ApplicationException exception) {
        ErrorDetail errorDetail = toErrorDetail(exception);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(errorDetail))
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public final Mono<ResponseEntity<ErrorResponse>> handleServerWebInputException(ServerWebInputException exception) {
        ErrorDetail errorDetail = toErrorDetail(FormatException.Type.ERROR_VALOR_ATRIBUTO_INVALIDO.build());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Collections.singletonList(errorDetail))
                .build();

        if (exception instanceof WebExchangeBindException webExchangeException &&
                webExchangeException.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST)) {
            String messageError = getMessagesErrorAnotations(webExchangeException);
            if (messageError != null) {
                errorDetail.setDetail(messageError);
            }
        }
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    private String getMessagesErrorAnotations(WebExchangeBindException webExchangeException) {
        if (!webExchangeException.getFieldErrors().isEmpty()) {
            StringBuilder errorDesc = new StringBuilder();
            FieldError error = webExchangeException.getFieldErrors().get(0);
            errorDesc.append(error.getDefaultMessage()).append(".");
            return errorDesc.toString();
        }
        return null;
    }

    private ErrorDetail toErrorDetail(ApplicationException exception) {
        return ErrorDetail.builder()
                .id(exception.getId())
                .type(exception.getErrorType().name())
                .message(exception.getMessage())
                .detail(exception.getDetail())
                .build();
    }
}
