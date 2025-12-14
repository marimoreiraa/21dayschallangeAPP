package com.ifmg.a21dayschallangeapp;

import java.util.List;

public class OverallProgressModel {
    private List<ProgressDay> progressData;

    public List<ProgressDay> getProgressData() {
        return progressData;
    }

    public static class ProgressDay {
        private String date;
        private int count;

        public String getDate() { return date; }
        public int getCount() { return count; }
    }
}