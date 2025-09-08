package com.somjeed.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Chatbot response payload")
public class ChatReply {

    @Builder.Default
    @ArraySchema(schema = @Schema(description = "Messages from chatbot", example = "Hello! How can I help you today?"))
    private List<String> messages = new ArrayList<>();
}