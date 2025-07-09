import com.sura.web.error.dto.ErrorDetail;

public class ErrorDetailTestDataBuilder {
    private String id;
    private String type;
    private String message;
    private String detail;

    public ErrorDetailTestDataBuilder() {
        this.id = "defaultId";
        this.type = "defaultType";
        this.message = "defaultMessage";
        this.detail = "defaultDetail";
    }

    public ErrorDetailTestDataBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ErrorDetailTestDataBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public ErrorDetailTestDataBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorDetailTestDataBuilder withDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public ErrorDetail build() {
        return ErrorDetail.builder()
                .id(this.id)
                .type(this.type)
                .message(this.message)
                .detail(this.detail)
                .build();
    }
}