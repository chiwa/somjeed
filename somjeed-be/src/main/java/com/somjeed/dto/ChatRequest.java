package com.somjeed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Chat request payload")
public class ChatRequest {
    @NotBlank
    @Schema(description = "Session ID of the chat", example = "abc-123")
    private String sessionId;

    @Schema(description = "Whether this is the first message in the session", example = "true")
    private boolean firstMessage;

    @Schema(description = "Message text from the user", example = "Yes")
    private String message;

    @AssertTrue(message = "message is required when firstMessage=false")
    public boolean isValid() {
        return firstMessage || (message != null && !message.trim().isEmpty());
    }
}
