package com.fantasyfootball.fantasy_analyzer.common.exception;

/**
 * Base exception for all Fantasy Analyzer domain exceptions.
 * All custom exceptions should extend this class.
 */
public class FantasyAnalyzerException extends RuntimeException {

    private final String errorCode;

    public FantasyAnalyzerException(String message) {
        super(message);
        this.errorCode = "FANTASY_ERROR";
    }

    public FantasyAnalyzerException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FantasyAnalyzerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FANTASY_ERROR";
    }

    public FantasyAnalyzerException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
