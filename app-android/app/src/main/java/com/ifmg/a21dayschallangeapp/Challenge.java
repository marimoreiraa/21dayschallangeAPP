package com.ifmg.a21dayschallangeapp;

import org.json.JSONObject;

public class Challenge {

    private int id;
    private String title;
    private String description;
    private String category;
    private String iconName;
    private int daysCompleted;

    private boolean isCheckedToday;

    public Challenge(JSONObject json) {
        this.id = json.optInt("id");
        this.title = json.optString("title");
        this.description = json.optString("description");
        this.category = json.optString("category");
        this.iconName = json.optString("icon_name", "ic_default");
        this.daysCompleted = json.optInt("days_completed", 0);
        this.isCheckedToday = json.optBoolean("is_checked_today", false);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getIconName() {
        return iconName;
    }

    public int getDaysCompleted() {
        return daysCompleted;
    }

    public boolean isCheckedToday() {
        return isCheckedToday;
    }

}