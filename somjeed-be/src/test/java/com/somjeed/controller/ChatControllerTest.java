package com.somjeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somjeed.exception.GlobalExceptionHandler;
import com.somjeed.service.GreetingService;
import com.somjeed.service.IntentDetectionService;
import com.somjeed.service.IntentPredictionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ChatController.class)
@Import(GlobalExceptionHandler.class)
class ChatControllerWebMvcTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Autowired
    GreetingService greetingService;

    @Autowired
    IntentPredictionService predictionService;

    @Autowired
    IntentDetectionService detectionService;

    @TestConfiguration
    static class MockConfig {
        @Bean GreetingService greetingService() { return Mockito.mock(GreetingService.class); }
        @Bean IntentPredictionService predictionService() { return Mockito.mock(IntentPredictionService.class); }
        @Bean IntentDetectionService detectionService() { return Mockito.mock(IntentDetectionService.class); }
    }

    @Test
    void greetingAndPrediction() throws Exception {
        Mockito.when(greetingService.composeGreeting())
                .thenReturn("Good morning, stay dry out there!");
        Mockito.when(predictionService.predict("s1"))
                .thenReturn("Looks like your payment is overdue. Would you like to check your current outstanding balance?");

        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"sessionId":"s1","firstMessage":true}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages[0]").value("Good morning, stay dry out there!"))
                .andExpect(jsonPath("$.messages[1]").value("Looks like your payment is overdue. Would you like to check your current outstanding balance?"));
    }

    @Test
    void yesAnswersPrediction() throws Exception {
        Mockito.when(predictionService.canAnswer("s1")).thenReturn(true);
        Mockito.when(predictionService.answerForYes("s1"))
                .thenReturn("Your current outstanding balance is 120,000 THB, and your due date was 1 September 2025.");

        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"sessionId":"s1","firstMessage":false,"message":"Yes"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Your current outstanding balance is 120,000 THB, and your due date was 1 September 2025."));
    }

    @Test
    void nudgeFlow() throws Exception {
        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"sessionId":"s1","firstMessage":false,"message":"#nudge"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Do you need any further assistance?"));
    }

    @Test
    void goodbyeFlow() throws Exception {
        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"sessionId":"s1","firstMessage":false,"message":"#goodbye"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages[0]").value("Thanks for chatting with me today."))
                .andExpect(jsonPath("$.messages[1]").value("Before you go, could you rate your experience?"));
    }

    @Test
    void detectionFlow() throws Exception {
        Mockito.when(detectionService.detection("statement"))
                .thenReturn("Your latest e-statement : https://scb.cardx.bank/statement.pdf");

        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"sessionId":"s1","firstMessage":false,"message":"statement"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0]").value("Your latest e-statement : https://scb.cardx.bank/statement.pdf"));
    }


    @Test
    void missingSessionId_ShouldReturn400() throws Exception {
        // firstMessage=true → ไม่ require message แต่ต้องมี sessionId
        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"firstMessage":true}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void missingMessageOnNonFirst_ShouldReturn400() throws Exception {
        mvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"sessionId":"s1","firstMessage":false}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages[*]", hasSize(1)));
    }
}