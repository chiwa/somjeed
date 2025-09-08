package com.somjeed.repository;

import com.somjeed.entry.FeedbackEntry;
import java.util.List;

public interface FeedbackRepository {
    void save(FeedbackEntry entry);
    List<FeedbackEntry> findAll();
    List<FeedbackEntry> findBySessionId(String sessionId);
}
