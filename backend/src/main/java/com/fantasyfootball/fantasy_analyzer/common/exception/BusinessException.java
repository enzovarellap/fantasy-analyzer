package com.fantasyfootball.fantasy_analyzer.common.exception;

/**
 * Exception thrown when a business rule is violated.
 */
public class BusinessException extends FantasyAnalyzerException {

    public BusinessException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessException(String message, String errorCode) {
        super(message, errorCode);
    }
}
