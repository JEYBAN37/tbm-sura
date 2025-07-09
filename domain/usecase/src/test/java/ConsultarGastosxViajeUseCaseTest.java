
import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.Parametros;
import com.sura.model.gastoxviaje.gateway.GastoxViajeRepository;
import com.sura.usecase.gastoxviaje.ConsultarGastosxViajeUseCase;
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
import java.util.Collections; // Importar para Collections.emptyList() o List.of()
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ConsultarGastosxViajeUseCaseTest {

    @Mock
    private GastoxViajeRepository gastoxViajeRepository;

    @InjectMocks
    private ConsultarGastosxViajeUseCase consultarGastosxViajeUseCase;

    private Parametros parametros;
    private GastoEmpleadoDto gasto1;
    private GastoEmpleadoDto gasto2;

    @BeforeEach
    void setUp() {
        // Inicializa los objetos comunes para las pruebas
        parametros = new Parametros();
        parametros.setPage(0);
        parametros.setSize(10);
        parametros.setIdEmpleado("EMP001"); // Nuevo campo idEmpleado

        // Asumiendo una estructura simple para GastoMensualDto para los mocks
        // Si GastoMensualDto tiene campos, deberías inicializarlos también.
        // Ejemplo: new GastoMensualDto("Enero", 150.0)

        // Ajusta la inicialización de GastoEmpleadoDto para coincidir con la nueva estructura
        gasto1 = new GastoEmpleadoDto("DNI123", "Juan Perez", Collections.emptyList()); // Puedes añadir datos reales de GastoMensualDto si son relevantes para otras pruebas
        gasto2 = new GastoEmpleadoDto("DNI456", "Maria Lopez", Collections.emptyList());
    }

    @Test
    @DisplayName("Debe consultar gastos por mes exitosamente cuando el repositorio devuelve datos")
    void consultarGastoxMes_shouldReturnGastosSuccessfully() {
        // ... el resto de la prueba sigue siendo igual ...
        List<GastoEmpleadoDto> gastosEsperados = Arrays.asList(gasto1, gasto2);
        when(gastoxViajeRepository.listarGastosxPersona(any(Parametros.class)))
                .thenReturn(Flux.fromIterable(gastosEsperados));

        Flux<GastoEmpleadoDto> resultado = consultarGastosxViajeUseCase.consultarGastoxMes(parametros);

        StepVerifier.create(resultado)
                .expectNext(gasto1)
                .expectNext(gasto2)
                .verifyComplete();

        verify(gastoxViajeRepository, times(1)).listarGastosxPersona(any(Parametros.class));
    }

    @Test
    @DisplayName("Debe propagar el error cuando el repositorio lanza una excepción")
    void consultarGastoxMes_shouldPropagateError() {
        // ... el resto de la prueba sigue siendo igual ...
        RuntimeException expectedException = new RuntimeException("Error simulado del repositorio");
        when(gastoxViajeRepository.listarGastosxPersona(any(Parametros.class)))
                .thenReturn(Flux.error(expectedException));

        Flux<GastoEmpleadoDto> resultado = consultarGastosxViajeUseCase.consultarGastoxMes(parametros);

        StepVerifier.create(resultado)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error simulado del repositorio"))
                .verify();

        verify(gastoxViajeRepository, times(1)).listarGastosxPersona(any(Parametros.class));
    }
}