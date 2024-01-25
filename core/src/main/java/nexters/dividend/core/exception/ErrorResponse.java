package nexters.dividend.core.exception;

public record ErrorResponse(
        int code,
        String message
) {
}