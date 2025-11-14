package com.fantasyfootball.fantasy_analyzer.common.exception;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends FantasyAnalyzerException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }

    public ValidationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message),
                "VALIDATION_ERROR");
    }
}
