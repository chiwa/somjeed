package com.somjeed.service;

import com.somjeed.constant.WeatherCondition;
import com.somjeed.weather.WeatherClient;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GreetingServiceTest {

    // สร้าง clock ให้ “เวลาที่กรุงเทพ” เป็นช่วงที่ต้องการ
    private Clock bangkokAt(String isoInstantUtc) {
        // ใช้ UTC instant เดียวกัน แต่ GreetingService จะ withZoneSameInstant("Asia/Bangkok")
        return Clock.fixed(Instant.parse(isoInstantUtc), ZoneId.of("UTC"));
    }

    @Test
    void morning_sunny() {
        // 08:00 ที่กรุงเทพฯ = 01:00 UTC (วันที่เดียวกัน)
        Clock fixed = bangkokAt("2025-09-06T01:00:00Z");
        WeatherClient weather = mock(WeatherClient.class);
        when(weather.getCondition()).thenReturn(WeatherCondition.SUNNY);

        GreetingService svc = new GreetingService(weather, fixed);
        String s = svc.composeGreeting();

        assertThat(s).startsWith("Good morning")
                .contains("on a sunshine day!");
    }

    @Test
    void afternoon_cloudy() {
        // 13:30 ที่กรุงเทพฯ = 06:30 UTC
        Clock fixed = bangkokAt("2025-09-06T06:30:00Z");
        WeatherClient weather = mock(WeatherClient.class);
        when(weather.getCondition()).thenReturn(WeatherCondition.CLOUDY);

        GreetingService svc = new GreetingService(weather, fixed);
        String s = svc.composeGreeting();

        assertThat(s).startsWith("Good afternoon")
                .contains("a bit cloudy");
    }

    @Test
    void evening_rainy() {
        // 19:45 ที่กรุงเทพฯ = 12:45 UTC
        Clock fixed = bangkokAt("2025-09-06T12:45:00Z");
        WeatherClient weather = mock(WeatherClient.class);
        when(weather.getCondition()).thenReturn(WeatherCondition.RAINY);

        GreetingService svc = new GreetingService(weather, fixed);
        String s = svc.composeGreeting();

        assertThat(s).startsWith("Good evening")
                .contains("stay dry out there!");
    }

    @Test
    void default_when_unknown_condition() {
        // 22:00 ที่กรุงเทพฯ = 15:00 UTC
        Clock fixed = bangkokAt("2025-09-06T15:00:00Z");
        WeatherClient weather = mock(WeatherClient.class);
        when(weather.getCondition()).thenReturn(WeatherCondition.UNKNOWN);

        GreetingService svc = new GreetingService(weather, fixed);
        String s = svc.composeGreeting();

        assertThat(s).startsWith("Good evening")
                .contains("I'm here to help!");
    }
}