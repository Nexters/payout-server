package nexters.payout.core.exception;

public record ErrorResponse(
        int code,
        String message
) {
}