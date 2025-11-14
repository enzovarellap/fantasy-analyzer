package com.fantasyfootball.fantasy_analyzer.common.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ApiResponse class.
 */
class ApiResponseTest {

    @Test
    void shouldCreateSuccessResponse() {
        // Given
        String data = "test data";

        // When
        ApiResponse<String> response = ApiResponse.success(data);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getError()).isNull();
    }

    @Test
    void shouldCreateSuccessResponseWithMessage() {
        // Given
        String data = "test data";
        String message = "Operation successful";

        // When
        ApiResponse<String> response = ApiResponse.success(data, message);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void shouldCreateErrorResponse() {
        // Given
        String message = "Error occurred";
        String errorCode = "ERROR_001";

        // When
        ApiResponse<Void> response = ApiResponse.error(message, errorCode);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getError()).isNotNull();
        assertThat(response.getError().getCode()).isEqualTo(errorCode);
        assertThat(response.getError().getMessage()).isEqualTo(message);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void shouldCreateErrorResponseWithDetails() {
        // Given
        ApiResponse.ErrorDetails errorDetails = ApiResponse.ErrorDetails.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .details("Field 'email' is required")
                .build();

        // When
        ApiResponse<Void> response = ApiResponse.error(errorDetails);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError()).isEqualTo(errorDetails);
        assertThat(response.getError().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getError().getDetails()).isEqualTo("Field 'email' is required");
    }

    @Test
    void timestampShouldBeCloseToNow() {
        // When
        ApiResponse<String> response = ApiResponse.success("data");

        // Then
        LocalDateTime now = LocalDateTime.now();
        assertThat(response.getTimestamp()).isBetween(
                now.minusSeconds(1),
                now.plusSeconds(1)
        );
    }
}
