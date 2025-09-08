package com.somjeed.repository;

import com.somjeed.entry.FeedbackEntry;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryFeedbackRepository implements FeedbackRepository {

    private final ArrayList<FeedbackEntry> store = new ArrayList<>();

    @Override
    public void save(FeedbackEntry entry) { store.add(entry); }

    @Override
    public List<FeedbackEntry> findAll() { return List.copyOf(store); }

    @Override
    public List<FeedbackEntry> findBySessionId(String sessionId) {
        return store.stream().filter(e -> e.getSessionId().equals(sessionId)).toList();
    }

}