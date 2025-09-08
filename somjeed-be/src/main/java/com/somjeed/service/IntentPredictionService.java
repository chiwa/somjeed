package com.somjeed.service;

import com.somjeed.intent.IntentPredictionClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class IntentPredictionService {

    private final IntentPredictionClient client;
    private static Map<String, String> answerForYes = new ConcurrentHashMap<>();

    public String predict(String sessionId) {
        Map<String, String> prediction = client.getIntentPrediction();
        answerForYes.put(sessionId, prediction.get("answer"));
        return prediction.get("message");
    }

    public boolean canAnswer(String sessionId) {
        return answerForYes.containsKey(sessionId);
    }

    public String answerForYes(String sessionId) {
        String result = answerForYes.get(sessionId);
        answerForYes.remove(sessionId);
        return result;
    }
}