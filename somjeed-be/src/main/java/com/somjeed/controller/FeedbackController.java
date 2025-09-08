package com.somjeed.controller;

import com.somjeed.dto.FeedbackRequest;
import com.somjeed.dto.FeedbackResponse;
import com.somjeed.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
@Slf4j
@Tag(name = "Feedback API", description = "Collect and store user feedback after chatbot session")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(
            summary = "Submit user feedback",
            description = "Save a feedback record including sessionId, rating, and optional comment.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Feedback recorded successfully",
                            content = @Content(schema = @Schema(implementation = FeedbackResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<FeedbackResponse> create(@Valid @RequestBody FeedbackRequest req) {
        feedbackService.saveFeedback(req);
        log.info("Feedback accepted: sessionId={}, rating={}", req.getSessionId(), req.getRating());
        log.info("Feedback from repository : {}", feedbackService.getBySessionId(req.getSessionId()));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new FeedbackResponse("Thanks! Your feedback has been recorded."));
    }
}
