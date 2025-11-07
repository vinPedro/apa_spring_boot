package apa.ifpb.edu.br.APA.exception;

import java.time.LocalDateTime;

public record ErrorMessage(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public ErrorMessage(int statusCode, String message) {
        this(statusCode, message, LocalDateTime.now());
    }
}
