package com.somjeed.entry;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackEntry {
    private String id;
    private String sessionId;
    private Integer rating;
    private String comment;
    private String source;
    private LocalDateTime createdDateTime;
}