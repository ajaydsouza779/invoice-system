package com.egdk.invoicesystem.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Schema(description = "Details about validation errors")
@Getter @Setter
public class ValidationErrorResponse extends ErrorResponse{
    private Map<String, String> validationErrors;

    public ValidationErrorResponse(int status, String message, Map<String, String> validationErrors) {
        super(status, message);
        this.validationErrors = validationErrors;
    }

}
