package com.somjeed.intent;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IntentDetectionClientMock implements IntentDetectionClient {

    private static final String FALLBACK =
            "sorry, i only know: call center, statement, lost card, points, redeem, or balance.";

    // กติกาเรียงลำดับความสำคัญบนลงล่าง (เจอก่อนตอบก่อน)
    private static final List<Rule> RULES = List.of(
            new Rule(List.of("statement", "billing", "bill"),
                    "here is your e-statement: https://xxx.bank/statement.pdf"),
            new Rule(List.of("lost card", "card missing"),
                    "Urgently!!!!! Please call 1111 to support."),
            new Rule(List.of("point", "points", "rewards"),
                    "you have 15,000 points."),
            new Rule(List.of("redeem", "redemption"),
                    "Please go to https://xxx.bank/redeem for redeem."),
            new Rule(List.of("balance", "available"),
                    "your balance about 45,000 thb and credit left 20,000 thb."),
            new Rule(List.of("call center"),
                    "Call Center number is 1111"),
            new Rule(List.of("hello"),
                    "Hello World"),
            new Rule(List.of("chiwa"),
                    "Chiwa Kantawong")
    );

    @Override
    public String getIntentDetection(String q) {
        if (q == null || q.isBlank()) {
            return "sorry, i don’t get it.";
        }
        final String text = q.toLowerCase();

        return RULES.stream()
                .filter(r -> r.matches(text))
                .findFirst()
                .map(Rule::response)
                .orElse(FALLBACK);
    }

    private record Rule(List<String> keywords, String response) {
        boolean matches(String text) {
            return keywords.stream().anyMatch(text::contains);
        }
    }
}