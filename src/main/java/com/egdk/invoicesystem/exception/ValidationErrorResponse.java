package com.egdk.invoicesystem.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class ValidationErrorResponse extends ErrorResponse{
    private Map<String, String> validationErrors;

    public ValidationErrorResponse(int status, String message, Map<String, String> validationErrors) {
        super(status, message);
        this.validationErrors = validationErrors;
    }

}
