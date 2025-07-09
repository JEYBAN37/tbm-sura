

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sura.model.common.ex.TechnicalException;
import com.sura.model.empleado.dto.GastoEmpleadoDto;
import com.sura.model.gastoxmes.Parametros;
import com.sura.r2dbc.GastoxViajeR2dbcAdapter;
import io.r2dbc.spi.Row;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness; // Importar Strictness
import org.mockito.junit.jupiter.MockitoSettings; // Importar MockitoSettings
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.function.BiFunction;


import static com.sura.model.empleado.Const.SQL_EMPLEADOS_PAGINADOS;
import static com.sura.model.empleado.Const.SQL_ORDER_LIMIT_OFFSET;
import static com.sura.model.empleado.Const.SQL_SELECT_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // <--- ¡Añade esta línea!
class GastosxViajeR2dbcAdapterTest {

    @Mock
    private R2dbcEntityTemplate entityTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GastoxViajeR2dbcAdapter gastoxViajeR2dbcAdapter;

    @Mock
    private DatabaseClient databaseClient;
    @Mock
    private GenericExecuteSpec genericExecuteSpec;
    @Mock
    private GenericExecuteSpec bindSpec;
    @Mock
    private RowsFetchSpec rowsFetchSpec;

    private Parametros parametros;
    private GastoEmpleadoDto gastoEmpleadoDto;

    @BeforeEach
    void setUp() {
        parametros = new Parametros();
        parametros.setPage(0);
        parametros.setSize(10);
        parametros.setIdEmpleado(null);

        gastoEmpleadoDto = new GastoEmpleadoDto("DNI123", "Juan Perez", Collections.emptyList());

        // Este stubbing se mantiene aquí porque es la primera llamada en la cadena de R2DBC
        // y es el punto de entrada principal para las pruebas de listarGastosxPersona.
        // Al usar Strictness.LENIENT, Mockito no se quejará si no se usa en otras pruebas.
        when(entityTemplate.getDatabaseClient()).thenReturn(databaseClient);
    }

    // --- Tests para listarGastosxPersona ---

    @Test
    @DisplayName("Debe listar gastos por persona sin ID de empleado y con paginación por defecto")
    void listarGastosxPersona_noIdEmpleado_defaultPagination() throws JsonProcessingException {
        // GIVEN
        parametros.setIdEmpleado(null);
        parametros.setPage(null);
        parametros.setSize(null);

        String expectedSql = SQL_EMPLEADOS_PAGINADOS + SQL_ORDER_LIMIT_OFFSET + SQL_SELECT_JSON;

        // Configurar los mocks específicos para esta cadena de ejecución
        when(databaseClient.sql(expectedSql)).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.bind(anyString(), any())).thenReturn(bindSpec);
        when(bindSpec.bind(anyString(), any())).thenReturn(bindSpec);
        // ¡CAMBIO AQUÍ! Usar any(BiFunction.class)
        when(bindSpec.map(any(java.util.function.BiFunction.class))).thenReturn(rowsFetchSpec);
        when(rowsFetchSpec.all()).thenReturn(Flux.just(gastoEmpleadoDto));

        when(objectMapper.readValue(anyString(), eq(GastoEmpleadoDto.class))).thenReturn(gastoEmpleadoDto);

        // WHEN
        Flux<GastoEmpleadoDto> result = gastoxViajeR2dbcAdapter.listarGastosxPersona(parametros);

        // THEN
        StepVerifier.create(result)
                .expectNext(gastoEmpleadoDto)
                .verifyComplete();

        verify(databaseClient, times(1)).sql(expectedSql);

        ArgumentCaptor<String> bindNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> bindValueCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(genericExecuteSpec, times(1)).bind(bindNameCaptor.capture(), bindValueCaptor.capture());
        verify(bindSpec, times(1)).bind(bindNameCaptor.capture(), bindValueCaptor.capture());

        assertEquals("limit", bindNameCaptor.getAllValues().get(0));
        assertEquals(10, bindValueCaptor.getAllValues().get(0));

        assertEquals("offset", bindNameCaptor.getAllValues().get(1));
        assertEquals(0, bindValueCaptor.getAllValues().get(1));
    }

    @Test
    @DisplayName("Debe listar gastos por persona con ID de empleado y paginación personalizada")
    void listarGastosxPersona_withIdEmpleado_customPagination() throws JsonProcessingException {
        // GIVEN
        parametros.setIdEmpleado("EMP001");
        parametros.setPage(2);
        parametros.setSize(5);

        String expectedSql = SQL_EMPLEADOS_PAGINADOS + " AND e.dniempleado = :dniempleado " + SQL_ORDER_LIMIT_OFFSET + SQL_SELECT_JSON;
        int expectedOffset = 2 * 5;

        // Configurar los mocks específicos para esta cadena de ejecución
        when(databaseClient.sql(expectedSql)).thenReturn(genericExecuteSpec);
        when(genericExecuteSpec.bind(eq("dniempleado"), eq("EMP001"))).thenReturn(bindSpec);
        when(bindSpec.bind(anyString(), any())).thenReturn(bindSpec);
        when(bindSpec.bind(anyString(), any())).thenReturn(bindSpec);
        // ¡CAMBIO AQUÍ! Usar any(BiFunction.class)
        when(bindSpec.map(any(BiFunction.class))).thenReturn(rowsFetchSpec);
        when(rowsFetchSpec.all()).thenReturn(Flux.just(gastoEmpleadoDto));

        when(objectMapper.readValue(anyString(), eq(GastoEmpleadoDto.class))).thenReturn(gastoEmpleadoDto);

        // WHEN
        Flux<GastoEmpleadoDto> result = gastoxViajeR2dbcAdapter.listarGastosxPersona(parametros);

        // THEN
        StepVerifier.create(result)
                .expectNext(gastoEmpleadoDto)
                .verifyComplete();

        verify(databaseClient, times(1)).sql(expectedSql);

        verify(genericExecuteSpec, times(1)).bind(eq("dniempleado"), eq("EMP001"));

        ArgumentCaptor<String> bindNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> bindValueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(bindSpec, times(2)).bind(bindNameCaptor.capture(), bindValueCaptor.capture());

        assertEquals("limit", bindNameCaptor.getAllValues().get(0));
        assertEquals(5, bindValueCaptor.getAllValues().get(0));

        assertEquals("offset", bindNameCaptor.getAllValues().get(1));
        assertEquals(expectedOffset, bindValueCaptor.getAllValues().get(1));
    }

    // --- Tests para mapRowToDto (usando Reflection para probar método privado) ---

    @Test
    @DisplayName("mapRowToDto debe mapear Row a GastoEmpleadoDto exitosamente")
    void mapRowToDto_shouldMapSuccessfully() throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // GIVEN
        String jsonResult = "{\"dniEmpleado\": \"DNI123\", \"nombreEmpleado\": \"Juan Perez\", \"meses\": []}";
        Row row = mock(Row.class);
        when(row.get("resultado", String.class)).thenReturn(jsonResult);

        when(objectMapper.readValue(jsonResult, GastoEmpleadoDto.class)).thenReturn(gastoEmpleadoDto);

        Method mapRowToDtoMethod = GastoxViajeR2dbcAdapter.class.getDeclaredMethod("mapRowToDto", Row.class);
        mapRowToDtoMethod.setAccessible(true);

        // WHEN
        GastoEmpleadoDto result = (GastoEmpleadoDto) mapRowToDtoMethod.invoke(gastoxViajeR2dbcAdapter, row);

        // THEN
        assertNotNull(result);
        assertEquals(gastoEmpleadoDto, result);
        verify(objectMapper, times(1)).readValue(jsonResult, GastoEmpleadoDto.class);
        verify(row, times(1)).get("resultado", String.class);
    }

    @Test
    @DisplayName("mapRowToDto debe lanzar TechnicalException cuando el JSON es inválido")
    void mapRowToDto_shouldThrowTechnicalExceptionForInvalidJson() throws JsonProcessingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // GIVEN
        String invalidJson = "invalid json string";
        Row row = mock(Row.class);
        when(row.get("resultado", String.class)).thenReturn(invalidJson);

        when(objectMapper.readValue(invalidJson, GastoEmpleadoDto.class)).thenThrow(mock(JsonProcessingException.class));

        Method mapRowToDtoMethod = GastoxViajeR2dbcAdapter.class.getDeclaredMethod("mapRowToDto", Row.class);
        mapRowToDtoMethod.setAccessible(true);

        // WHEN & THEN
        InvocationTargetException wrappedException = assertThrows(InvocationTargetException.class, () ->
                mapRowToDtoMethod.invoke(gastoxViajeR2dbcAdapter, row)
        );
        assertTrue(wrappedException.getCause() instanceof TechnicalException);
        TechnicalException thrown = (TechnicalException) wrappedException.getCause();

        assertEquals(TechnicalException.class, thrown.getClass());
        verify(objectMapper, times(1)).readValue(invalidJson, GastoEmpleadoDto.class);
    }

    // --- Tests para getSql (usando Reflection para probar método privado) ---

    @Test
    @DisplayName("getSql debe construir el SQL correcto sin ID de empleado")
    void getSql_noIdEmpleado_shouldBuildCorrectSql() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // GIVEN
        parametros.setIdEmpleado(null);
        String expectedSql = SQL_EMPLEADOS_PAGINADOS + SQL_ORDER_LIMIT_OFFSET + SQL_SELECT_JSON;

        Method getSqlMethod = GastoxViajeR2dbcAdapter.class.getDeclaredMethod("getSql", Parametros.class);
        getSqlMethod.setAccessible(true);

        // WHEN
        StringBuilder sqlBuilder = (StringBuilder) getSqlMethod.invoke(gastoxViajeR2dbcAdapter, parametros);

        // THEN
        assertEquals(expectedSql, sqlBuilder.toString());
    }

    @Test
    @DisplayName("getSql debe construir el SQL correcto con ID de empleado")
    void getSql_withIdEmpleado_shouldBuildCorrectSql() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // GIVEN
        parametros.setIdEmpleado("EMP001");
        String expectedSql = SQL_EMPLEADOS_PAGINADOS + " AND e.dniempleado = :dniempleado " + SQL_ORDER_LIMIT_OFFSET + SQL_SELECT_JSON;

        Method getSqlMethod = GastoxViajeR2dbcAdapter.class.getDeclaredMethod("getSql", Parametros.class);
        getSqlMethod.setAccessible(true);

        // WHEN
        StringBuilder sqlBuilder = (StringBuilder) getSqlMethod.invoke(gastoxViajeR2dbcAdapter, parametros);

        // THEN
        assertEquals(expectedSql, sqlBuilder.toString());
    }

    @Test
    @DisplayName("getSql debe construir el SQL correcto con ID de empleado en blanco")
    void getSql_blankIdEmpleado_shouldBuildCorrectSql() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // GIVEN
        parametros.setIdEmpleado("   ");
        String expectedSql = SQL_EMPLEADOS_PAGINADOS + SQL_ORDER_LIMIT_OFFSET + SQL_SELECT_JSON;

        Method getSqlMethod = GastoxViajeR2dbcAdapter.class.getDeclaredMethod("getSql", Parametros.class);
        getSqlMethod.setAccessible(true);

        // WHEN
        StringBuilder sqlBuilder = (StringBuilder) getSqlMethod.invoke(gastoxViajeR2dbcAdapter, parametros);

        // THEN
        assertEquals(expectedSql, sqlBuilder.toString());
    }
}