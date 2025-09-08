package com.somjeed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Feedback API response")
public class FeedbackResponse {
    @Schema(description = "Response message from API", example = "Thanks! Your feedback has been recorded.")
    private String message;
}