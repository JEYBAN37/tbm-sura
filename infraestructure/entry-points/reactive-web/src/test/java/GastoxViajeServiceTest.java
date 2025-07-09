import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.Parametros;
import com.sura.usecase.gastoxviaje.ConsultarGastosxViajeUseCase;
import com.sura.web.gastoxviaje.GastoxViajeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GastoxViajeServiceTest {

    @Mock // Mock del caso de uso
    private ConsultarGastosxViajeUseCase consultarGastosxViajeUseCase;

    @InjectMocks // Inyecta el mock en la instancia del servicio
    private GastoxViajeService gastoxViajeService;

    private Parametros testParametros;
    private GastoEmpleadoDto gasto1;
    private GastoEmpleadoDto gasto2;

    @BeforeEach
    void setUp() {
        testParametros = new Parametros();
        testParametros.setIdEmpleado("12345");
        testParametros.setPage(0);
        testParametros.setSize(10);

        gasto1 = new GastoEmpleadoDto("123", "Empleado Uno", Collections.emptyList());
        gasto2 = new GastoEmpleadoDto("456", "Empleado Dos", Collections.emptyList());
    }

    @Test
    @DisplayName("Debe retornar un Flux de GastoEmpleadoDto cuando el caso de uso es exitoso y emite datos")
    void consultarGastos_shouldReturnFluxOfDtos_whenUseCaseEmitsData() {
        // GIVEN: El caso de uso retorna un Flux con dos DTOs de ejemplo
        List<GastoEmpleadoDto> expectedList = Arrays.asList(gasto1, gasto2);
        when(consultarGastosxViajeUseCase.consultarGastoxMes(testParametros))
                .thenReturn(Flux.fromIterable(expectedList));

        // WHEN: Se llama al método consultarGastos del servicio
        Flux<GastoEmpleadoDto> resultFlux = gastoxViajeService.consultarGastos(testParametros);

        // THEN: Verificar que el Flux emitido contiene los DTOs esperados y se completa
        StepVerifier.create(resultFlux)
                .expectNext(gasto1)
                .expectNext(gasto2)
                .verifyComplete();

        // Verificar que el caso de uso fue llamado exactamente una vez con los parámetros correctos
        verify(consultarGastosxViajeUseCase, times(1)).consultarGastoxMes(testParametros);
    }

    @Test
    @DisplayName("Debe retornar un Flux vacío cuando el caso de uso es exitoso pero no emite datos")
    void consultarGastos_shouldReturnEmptyFlux_whenUseCaseEmitsNoData() {
        // GIVEN: El caso de uso retorna un Flux vacío
        when(consultarGastosxViajeUseCase.consultarGastoxMes(testParametros))
                .thenReturn(Flux.empty());

        // WHEN: Se llama al método consultarGastos del servicio
        Flux<GastoEmpleadoDto> resultFlux = gastoxViajeService.consultarGastos(testParametros);

        // THEN: Verificar que el Flux se completa sin emitir ningún DTO
        StepVerifier.create(resultFlux)
                .expectComplete()
                .verify();

        // Verificar que el caso de uso fue llamado exactamente una vez con los parámetros correctos
        verify(consultarGastosxViajeUseCase, times(1)).consultarGastoxMes(testParametros);
    }

    @Test
    @DisplayName("Debe propagar el error cuando el caso de uso emite un error")
    void consultarGastos_shouldPropagateError_whenUseCaseEmitsError() {
        // GIVEN: El caso de uso retorna un Flux que emite un error
        RuntimeException expectedError = new RuntimeException("Error simulado en el caso de uso");
        when(consultarGastosxViajeUseCase.consultarGastoxMes(testParametros))
                .thenReturn(Flux.error(expectedError));

        // WHEN: Se llama al método consultarGastos del servicio
        Flux<GastoEmpleadoDto> resultFlux = gastoxViajeService.consultarGastos(testParametros);

        // THEN: Verificar que el Flux propaga el error esperado
        StepVerifier.create(resultFlux)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error simulado en el caso de uso"))
                .verify();

        // Verificar que el caso de uso fue llamado exactamente una vez con los parámetros correctos
        verify(consultarGastosxViajeUseCase, times(1)).consultarGastoxMes(testParametros);
    }

    @Test
    @DisplayName("Debe llamar al caso de uso con diferentes parámetros")
    void consultarGastos_shouldCallUseCaseWithDifferentParameters() {
        // GIVEN: Un nuevo conjunto de parámetros
        Parametros differentParametros = new Parametros();
        differentParametros.setIdEmpleado("67890");
        differentParametros.setPage(1);
        differentParametros.setSize(5);

        // Configurar el caso de uso para devolver un Flux vacío para simplificar esta prueba de invocación
        when(consultarGastosxViajeUseCase.consultarGastoxMes(differentParametros))
                .thenReturn(Flux.empty());

        // WHEN: Se llama al método consultarGastos con los nuevos parámetros
        Flux<GastoEmpleadoDto> resultFlux = gastoxViajeService.consultarGastos(differentParametros);

        // THEN: Verificar que el Flux se completa (el contenido no es el foco aquí)
        StepVerifier.create(resultFlux)
                .expectComplete()
                .verify();

        // Verificar que el caso de uso fue llamado exactamente una vez con los *nuevos* parámetros
        verify(consultarGastosxViajeUseCase, times(1)).consultarGastoxMes(differentParametros);
        // Asegurarse de que no fue llamado con los parámetros de setup si no corresponde
        verify(consultarGastosxViajeUseCase, never()).consultarGastoxMes(testParametros);
    }
}