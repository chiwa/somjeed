package com.somjeed.service;

import com.somjeed.dto.FeedbackRequest;
import com.somjeed.entry.FeedbackEntry;
import com.somjeed.repository.FeedbackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FeedbackService {

    private final FeedbackRepository repo;

    public void saveFeedback(FeedbackRequest req) {
        var entry = FeedbackEntry.builder()
                .id(UUID.randomUUID().toString())
                .sessionId(req.getSessionId())
                .rating(Integer.valueOf(req.getRating()))
                .comment(req.getComment())
                .createdDateTime(LocalDateTime.now())
                .build();
        repo.save(entry);
    }

    public List<FeedbackEntry> getBySessionId(String sessionId) {
        return repo.findBySessionId(sessionId);
    }
}
