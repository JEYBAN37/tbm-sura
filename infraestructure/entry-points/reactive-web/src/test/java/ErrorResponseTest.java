import com.sura.web.error.dto.ErrorDetail;
import com.sura.web.error.dto.ErrorResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorResponseTest {

    @Test
    void testErrorResponse() {
        ErrorDetail errorDetail = new ErrorDetailTestDataBuilder()
                .withId("123")
                .withType("Technical")
                .withMessage("Test message")
                .withDetail("Test detail")
                .build();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(List.of(errorDetail))
                .build();

        assertNotNull(errorResponse.getErrors());
        assertEquals(1, errorResponse.getErrors().size());
        assertEquals("123", errorResponse.getErrors().get(0).getId());
        assertEquals("Technical", errorResponse.getErrors().get(0).getType());
        assertEquals("Test message", errorResponse.getErrors().get(0).getMessage());
        assertEquals("Test detail", errorResponse.getErrors().get(0).getDetail());
    }
}