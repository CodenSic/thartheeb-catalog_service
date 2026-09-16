package com.thartheeb.catalog.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(name = "ApiError", description = "Uniform error response returned by every Thartheeb API")
public record ApiError(
    @Schema(example = "404") int status,
    @Schema(example = "Not Found") String error,
    @Schema(example = "User with id 123 not found") String message,
    String type, String title, String detail, String instance, String code,
    Instant timestamp, Map<String, String> errors, String path
) {
}
