package com.somjeed.weather;

import com.somjeed.constant.WeatherCondition;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class WeatherClientMock implements WeatherClient {

    @Override
    public WeatherCondition getCondition() {
        return switch (ThreadLocalRandom.current().nextInt(4)) {
            case 0 -> WeatherCondition.SUNNY;
            case 1 -> WeatherCondition.CLOUDY;
            case 2 -> WeatherCondition.RAINY;
            case 3 -> WeatherCondition.STORMY;
            default -> WeatherCondition.UNKNOWN;
        };
    }
}
