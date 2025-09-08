package com.somjeed.controller;

import com.somjeed.dto.ChatReply;
import com.somjeed.dto.ChatRequest;
import com.somjeed.service.GreetingService;
import com.somjeed.service.IntentDetectionService;
import com.somjeed.service.IntentPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
@Tag(name = "Chat API", description = "Somjeed chatbot endpoints")
public class ChatController {

    private final GreetingService greetingService;
    private final IntentPredictionService predictionService;
    private final IntentDetectionService detectionService;

    private static final Set<String> YES_WORDS = Set.of("yes", "y", "ok");

    @PostMapping
    @Operation(
            summary = "Chat with Somjeed",
            description = "Send a message to chatbot. Handles greeting, intent prediction, intent detection, and goodbye flow.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful response",
                            content = @Content(schema = @Schema(implementation = ChatReply.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error")
            }
    )
    public ResponseEntity<ChatReply> chat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Chat request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChatRequest.class))
            )
            @Validated @RequestBody ChatRequest req) {

        List<String> messages = new ArrayList<>();

        if (req.isFirstMessage()) {
            // 1) Greeting
            messages.add(greetingService.composeGreeting());
            // 2) Intent prediction
            messages.add(predictionService.predict(req.getSessionId()));
            return ResponseEntity.ok(new ChatReply(messages));
        }

        String msg = req.getMessage() == null ? "" : req.getMessage().trim().toLowerCase();

        // จัดการคำสั่งพิเศษด้วย switch
        switch (msg) {
            case "#nudge" -> messages.add("Do you need any further assistance?");
            case "#goodbye" -> {
                messages.add("Thanks for chatting with me today.");
                messages.add("Before you go, could you rate your experience?");
            }
            default -> {
                if (answerYesPrediction(req.getSessionId(), msg)) {
                    messages.add(predictionService.answerForYes(req.getSessionId()));
                } else {
                    // Intent detection ปกติ
                    messages.add(detectionService.detection(msg));
                }
            }
        }

        return ResponseEntity.ok(new ChatReply(messages));
    }

    private boolean answerYesPrediction(String sessionId, String message) {
        return predictionService.canAnswer(sessionId) && YES_WORDS.contains(message);
    }
}