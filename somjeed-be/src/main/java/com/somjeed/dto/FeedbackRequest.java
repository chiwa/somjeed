package com.somjeed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "User feedback request payload")
public class FeedbackRequest {
    @NotBlank
    @Schema(description = "Chat session identifier", example = "abc-123")
    private String sessionId;

    @NotNull
    @Schema(description = "Rating must be 1 (bad), 3 (okay), or 5 (good)", example = "5")
    @Pattern(regexp = "^^[135]$", message = "rating must be 1, 3, or 5")
    private String rating;

    @Schema(description = "Optional user comment", example = "Very helpful, thanks!")
    private String comment;
}