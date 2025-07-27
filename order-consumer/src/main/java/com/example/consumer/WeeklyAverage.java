// src/main/java/com/example/consumer/WeeklyAverage.java
package com.example.consumer;

public class WeeklyAverage {
    private final long weekStart;    // epoch millis at the start of the week
    private final double averageMs;  // average serve time in milliseconds

    public WeeklyAverage(long weekStart, double averageMs) {
        this.weekStart = weekStart;
        this.averageMs = averageMs;
    }

    public long getWeekStart() {
        return weekStart;
    }

    public double getAverageMs() {
        return averageMs;
    }
}