package com.somjeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somjeed.exception.GlobalExceptionHandler;
import com.somjeed.service.FeedbackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web MVC slice test for FeedbackController
 * - ใช้ @WebMvcTest เฉพาะ Controller
 * - Mock FeedbackService ผ่าน @TestConfiguration (เลี่ยง @MockBean ที่ deprecated)
 * - ใส่ GlobalExceptionHandler เพื่อเช็ค 400 จาก Bean Validation
 */
@WebMvcTest(FeedbackController.class)
@Import(GlobalExceptionHandler.class)
class FeedbackControllerWebMvcTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Autowired
    FeedbackService feedbackService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        FeedbackService feedbackService() {
            return Mockito.mock(FeedbackService.class);
        }
    }

    @Test
    void createFeedback_ok() throws Exception {
        // สมมติ FeedbackRequest ใช้ Integer rating = 5 (ถ้าโปรเจ็กต์พี่พีเป็น String '1|3|5' ก็แก้ JSON ตรงนี้ให้ตรง)
        String json = """
            {
              "sessionId": "abc-123",
              "rating": 5,
              "comment": "very helpful"
            }
            """;

        mvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Thanks! Your feedback has been recorded.")));

        // verify ว่ามีการเรียกบันทึกจริง 1 ครั้ง
        ArgumentCaptor<com.somjeed.dto.FeedbackRequest> captor =
                ArgumentCaptor.forClass(com.somjeed.dto.FeedbackRequest.class);
        verify(feedbackService, times(1)).saveFeedback(captor.capture());

        // เช็คค่าที่ส่งเข้า service
        com.somjeed.dto.FeedbackRequest saved = captor.getValue();
        assertThat(saved.getRating()).isEqualTo("5");
    }

    @Test
    void createFeedback_missingSessionId_400() throws Exception {
        // ขาด sessionId → @NotBlank fail
        String json = """
            {
              "rating": 5,
              "comment": "ok"
            }
            """;

        mvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void createFeedback_invalidRating_400() throws Exception {
        String json = """
            {
              "sessionId": "abc-123",
              "rating": 2,
              "comment": "not allowed rating"
            }
            """;

        mvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"))
                .andExpect(jsonPath("$.messages").isArray());
    }
}