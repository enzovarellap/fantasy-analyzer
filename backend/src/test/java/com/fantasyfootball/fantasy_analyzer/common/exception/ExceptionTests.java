package com.fantasyfootball.fantasy_analyzer.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for custom exceptions.
 */
class ExceptionTests {

    @Test
    void resourceNotFoundExceptionShouldHaveCorrectMessage() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("User", "id", 123);

        // Then
        assertThat(exception.getMessage()).isEqualTo("User not found with id: '123'");
        assertThat(exception.getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
    }

    @Test
    void businessExceptionShouldHaveCorrectErrorCode() {
        // When
        BusinessException exception = new BusinessException("Business rule violated");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Business rule violated");
        assertThat(exception.getErrorCode()).isEqualTo("BUSINESS_RULE_VIOLATION");
    }

    @Test
    void validationExceptionShouldHaveCorrectFormat() {
        // When
        ValidationException exception = new ValidationException("email", "must be valid");

        // Then
        assertThat(exception.getMessage()).contains("email");
        assertThat(exception.getMessage()).contains("must be valid");
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void unauthorizedExceptionShouldHaveDefaultMessage() {
        // When
        UnauthorizedException exception = new UnauthorizedException();

        // Then
        assertThat(exception.getMessage()).isEqualTo("You are not authorized to perform this action");
        assertThat(exception.getErrorCode()).isEqualTo("UNAUTHORIZED");
    }
}
