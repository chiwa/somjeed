package com.somjeed.service;

import com.somjeed.intent.IntentDetectionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IntentDetectionServiceTest {

    @Mock
    private IntentDetectionClient detectionClient;

    private IntentDetectionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new IntentDetectionService(detectionClient);
    }

    @Test
    void detectionDelegates() {
        String input = "please show my statement";
        String expected = "here is your e-statement: https://xxx.bank/statement.pdf";

        when(detectionClient.getIntentDetection(input)).thenReturn(expected);

        String actual = service.detection(input);

        assertThat(actual).isEqualTo(expected);
        verify(detectionClient, times(1)).getIntentDetection(input);
        verifyNoMoreInteractions(detectionClient);
    }

    @Test
    void detectionNull() {
        when(detectionClient.getIntentDetection(null)).thenReturn("sorry, i don’t get it.");

        String actual = service.detection(null);

        assertThat(actual).isEqualTo("sorry, i don’t get it.");
        verify(detectionClient).getIntentDetection(null);
    }
}