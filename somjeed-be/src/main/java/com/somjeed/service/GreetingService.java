package com.somjeed.service;

import com.somjeed.weather.WeatherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;

@Service
@RequiredArgsConstructor
public class GreetingService {

    private final WeatherClient weather;
    private final Clock clock;

    public String composeGreeting() {
        ZoneId tz = ZoneId.of("Asia/Bangkok");
        LocalTime now = ZonedDateTime.now(clock).withZoneSameInstant(tz).toLocalTime();

        String prefix = switch (timeBandOf(now)) {
            case MORNING -> "Good morning";
            case AFTERNOON -> "Good afternoon";
            case EVENING -> "Good evening";
        };

        String tail = switch (weather.getCondition()) {
            case SUNNY   -> "on a sunshine day!";
            case CLOUDY  -> "a bit cloudy but I'm here to help!";
            case RAINY   -> "stay dry out there!";
            case STORMY  -> "let me help make your stormy day better.";
            default      -> "I'm here to help!";
        };

        return prefix + ", " + tail;
    }

    enum TimeBand { MORNING, AFTERNOON, EVENING }
    private TimeBand timeBandOf(LocalTime t) {
        if (!t.isBefore(LocalTime.of(5,0)) && !t.isAfter(LocalTime.of(11,59,59))) return TimeBand.MORNING;
        if (!t.isBefore(LocalTime.of(12,0)) && !t.isAfter(LocalTime.of(16,59,59))) return TimeBand.AFTERNOON;
        return TimeBand.EVENING;
    }
}