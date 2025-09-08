package com.somjeed.service;

import com.somjeed.intent.IntentDetectionClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IntentDetectionService {

    private final IntentDetectionClient detectionClient;

    public String detection(String message) {
      return detectionClient.getIntentDetection(message);
    }
}
