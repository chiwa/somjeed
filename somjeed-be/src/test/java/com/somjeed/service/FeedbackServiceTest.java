package com.somjeed.service;

import com.somjeed.dto.FeedbackRequest;
import com.somjeed.entry.FeedbackEntry;
import com.somjeed.repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    FeedbackRepository repo;

    @InjectMocks
    FeedbackService service;

    @Test
    void saveFeedback_shouldMapFieldsAndPersist() {
        // arrange
        FeedbackRequest req = new FeedbackRequest();
        req.setSessionId("s-001");
        req.setRating("5");             // โค้ดปัจจุบัน parse จาก String
        req.setComment("great!");

        ArgumentCaptor<FeedbackEntry> captor = ArgumentCaptor.forClass(FeedbackEntry.class);

        // act
        service.saveFeedback(req);

        // assert
        verify(repo, times(1)).save(captor.capture());
        FeedbackEntry saved = captor.getValue();

        assertThat(saved.getId()).isNotBlank();                // ถูก gen ขึ้นมา
        assertThat(saved.getSessionId()).isEqualTo("s-001");
        assertThat(saved.getRating()).isEqualTo(5);
        assertThat(saved.getComment()).isEqualTo("great!");
        assertThat(saved.getCreatedDateTime()).isNotNull();
    }

    @Test
    void saveFeedback_shouldThrowWhenRatingIsNotNumeric() {
        FeedbackRequest req = new FeedbackRequest();
        req.setSessionId("s-002");
        req.setRating("x"); // ไม่ใช่ตัวเลข
        req.setComment(null);

        assertThatThrownBy(() -> service.saveFeedback(req))
                .isInstanceOf(NumberFormatException.class);

        verifyNoInteractions(repo); // ไม่ควรถูก save
    }

    @Test
    void getBySessionId_shouldDelegateToRepo() {
        when(repo.findBySessionId("s-003"))
                .thenReturn(List.of(FeedbackEntry.builder()
                        .id("id-1")
                        .sessionId("s-003")
                        .rating(3)
                        .comment(null)
                        .createdDateTime(LocalDateTime.now())
                        .build()));

        List<FeedbackEntry> result = service.getBySessionId("s-003");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSessionId()).isEqualTo("s-003");
        verify(repo, times(1)).findBySessionId("s-003");
    }
}