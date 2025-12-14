package com.ifmg.a21dayschallangeapp;

public class StatsModel {
    private int currentStreak;
    private int habitCompletionRate;
    private int skippedDays;
    private int daysRemaining;

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getHabitCompletionRate() {
        return habitCompletionRate;
    }

    public int getSkippedDays() {
        return skippedDays;
    }

    public int getDaysRemaining() {
        return daysRemaining;
    }
}