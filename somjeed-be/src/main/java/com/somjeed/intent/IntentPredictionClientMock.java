package com.somjeed.intent;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class IntentPredictionClientMock implements IntentPredictionClient {

    private static final String OVERDUE = "OVERDUE";
    private static final String PAYMENT_RECEIVED = "PAYMENT_RECEIVED";
    private static final String DUPLICATE_TXN = "DUPLICATE_TXN";
    private static final String MESSAGE = "message";
    private static final String ANSWER = "answer";
    private static final String INTENT = "intent";
    private final Random random = new Random();

    @Override
    public Map<String, String> getIntentPrediction() {
        String[] intents = {OVERDUE, PAYMENT_RECEIVED, DUPLICATE_TXN};
        String chosen = intents[random.nextInt(intents.length)];

        return switch (chosen) {
            case OVERDUE -> Map.of(
                    INTENT, OVERDUE,
                    MESSAGE, "Looks like your payment is overdue. Would you like to check your current outstanding balance?",
                    ANSWER, "Your current outstanding balance is 120,000 THB, and your due date was 1 September 2025."
            );
            case PAYMENT_RECEIVED -> Map.of(
                    INTENT, PAYMENT_RECEIVED,
                    MESSAGE, "Payment received today. Do you want to see your updated available credit?",
                    ANSWER, "Your available credit is 50,000 THB, and outstanding balance is 120,000 THB."
            );
            case DUPLICATE_TXN -> Map.of(
                    INTENT, DUPLICATE_TXN,
                    MESSAGE, "I notice two similar purchases close in time. Do you want to cancel or report one?",
                    ANSWER, "You have 2 transactions of 500 THB at BigC within 5 minutes. Do you want to cancel one?"
            );
            default -> Map.of();
        };
    }
}