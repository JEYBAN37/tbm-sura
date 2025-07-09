import com.sura.web.error.dto.ErrorDetail;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorDetailTest {

    @Test
    void testErrorDetail() {
        ErrorDetail errorDetail = new ErrorDetailTestDataBuilder()
                .withId("123")
                .withType("Technical")
                .withMessage("Test message")
                .withDetail("Test detail")
                .build();

        assertEquals("123", errorDetail.getId());
        assertEquals("Technical", errorDetail.getType());
        assertEquals("Test message", errorDetail.getMessage());
        assertEquals("Test detail", errorDetail.getDetail());
    }
}