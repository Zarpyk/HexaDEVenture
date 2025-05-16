package com.hexadeventure.application.port.in.users;

public interface StatsUseCase {
    double getAverageTime(String userId);
    double getAverageDistance(String userId);
    double getWinRate(String userId);
}
