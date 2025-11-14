package com.fantasyfootball.fantasy_analyzer.common.exception;

import com.fantasyfootball.fantasy_analyzer.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for consistent error responses across the application.
 * Handles all exceptions and converts them to standard ApiResponse format.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code(ex.getErrorCode())
                        .message(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        log.warn("Business exception: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code(ex.getErrorCode())
                        .message(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex,
            WebRequest request) {
        log.warn("Validation exception: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code(ex.getErrorCode())
                        .message(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle MethodArgumentNotValidException (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code("VALIDATION_ERROR")
                        .message("Validation failed for one or more fields")
                        .validationErrors(errors)
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            UnauthorizedException ex,
            WebRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code(ex.getErrorCode())
                        .message(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle BadCredentialsException (Authentication failure)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code("INVALID_CREDENTIALS")
                        .message("Invalid email or password")
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code("ACCESS_DENIED")
                        .message("You don't have permission to access this resource")
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            WebRequest request) {
        log.error("Unexpected error occurred", ex);

        ApiResponse<Void> response = ApiResponse.error(
                ApiResponse.ErrorDetails.builder()
                        .code("INTERNAL_SERVER_ERROR")
                        .message("An unexpected error occurred. Please try again later.")
                        .details(ex.getMessage())
                        .build()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
