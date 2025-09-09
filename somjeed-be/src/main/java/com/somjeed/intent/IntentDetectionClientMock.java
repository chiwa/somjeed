package com.somjeed.intent;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class IntentDetectionClientMock implements IntentDetectionClient {

    @Override
    public String getIntentDetection(String q) {
        if (q == null) {
            return "sorry, i don’t get it.";
        }
         q = q.toLowerCase();

        if (matchesAny(q, Arrays.asList("statement", "billing", "bill"))) {
            return "here is your e-statement: https://scb.cardx.bank/statement.pdf";
        }

        if (matchesAny(q, Arrays.asList("lost card", "card missing"))) {
            return "Urgently!!!!! Please call 1111 to support.";
        }

        if (matchesAny(q, Arrays.asList("point", "points", "rewards"))) {
            return "you have 15,000 points.";
        }

        if (matchesAny(q, Arrays.asList("redeem", "redemption"))) {
            return "Please go to https://scb.cardx.bank/redeem for redeem.";
        }

        if (matchesAny(q, Arrays.asList("balance", "available"))) {
            return "your balance about 45,000 thb and credit left 20,000 thb.";
        }

        if (matchesAny(q, Arrays.asList("call center"))) {
            return "Call Center number is 1111";
        }

        if (matchesAny(q, Arrays.asList("hello"))) {
            return "Hello World";
        }

        if (matchesAny(q, Arrays.asList("chiwa"))) {
            return "Chiwa Kantawong";
        }

        return "sorry, i only know: call center, statement, lost card, points, redeem, or balance.";
    }

    private boolean matchesAny(String text, List<String> keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }
}