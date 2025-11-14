package com.fantasyfootball.fantasy_analyzer.common.exception;

/**
 * Exception thrown when user is not authorized to perform an action.
 */
public class UnauthorizedException extends FantasyAnalyzerException {

    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }

    public UnauthorizedException() {
        super("You are not authorized to perform this action", "UNAUTHORIZED");
    }
}
