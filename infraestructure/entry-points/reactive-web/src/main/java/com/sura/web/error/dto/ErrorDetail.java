package com.sura.web.error.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ErrorDetail {
    private String id;
    private String type;
    private String message;
    private String detail;
}
