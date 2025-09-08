package com.somjeed.service;

import com.somjeed.intent.IntentPredictionClient;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class IntentPredictionServiceTest {

    private IntentPredictionService newServiceWithMockClient(Map<String, String> predictionPayload) {
        IntentPredictionClient client = mock(IntentPredictionClient.class);
        when(client.getIntentPrediction()).thenReturn(predictionPayload);
        return new IntentPredictionService(client);
    }

    @Test
    void predict_ShouldStoreOneTimeAnswer_AndReturnMessage() {
        var payload = Map.of(
                "intent", "OVERDUE",
                "message", "Looks like your payment is overdue.",
                "answer", "Your current outstanding balance is 120,000 THB."
        );
        var service = newServiceWithMockClient(payload);

        String session = UUID.randomUUID().toString();
        String message = service.predict(session);

        // ได้ข้อความ prediction กลับไปแสดงให้ user
        assertThat(message).isEqualTo("Looks like your payment is overdue.");
        // และมี one-time answer ถูกเก็บไว้รอ "Yes"
        assertThat(service.canAnswer(session)).isTrue();
    }

    @Test
    void answerForYes_ShouldReturnOnce_ThenBeRemoved() {
        var payload = Map.of(
                "message", "Payment received today. Do you want to see your updated available credit?",
                "answer", "Your available credit is 50,000 THB, and outstanding balance is 120,000 THB."
        );
        var service = newServiceWithMockClient(payload);

        String session = UUID.randomUUID().toString();
        service.predict(session);

        // ครั้งที่ 1: ได้คำตอบและถูกลบออกจากคลัง one-time
        String ans1 = service.answerForYes(session);
        assertThat(ans1).isEqualTo("Your available credit is 50,000 THB, and outstanding balance is 120,000 THB.");
        assertThat(service.canAnswer(session)).isFalse();

        // ครั้งที่ 2: ไม่มีคำตอบค้างอยู่แล้ว -> ควรเป็น null
        String ans2 = service.answerForYes(session);
        assertThat(ans2).isNull();
    }

    @Test
    void answerForYes_WhenNoPendingPrediction_ShouldReturnNull() {
        var payload = Map.of(
                "message", "Looks like your payment is overdue.",
                "answer", "Your current outstanding balance is 120,000 THB."
        );
        var service = newServiceWithMockClient(payload);

        // ยังไม่เคย predict สำหรับ session นี้เลย
        String session = UUID.randomUUID().toString();
        assertThat(service.canAnswer(session)).isFalse();

        // ตอบ yes ทั้งที่ไม่มี prediction ค้าง -> null
        String ans = service.answerForYes(session);
        assertThat(ans).isNull();
    }

}