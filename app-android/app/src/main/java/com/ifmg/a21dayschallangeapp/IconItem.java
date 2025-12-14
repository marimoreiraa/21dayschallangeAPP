package com.ifmg.a21dayschallangeapp;

public class IconItem {
    private String iconName;
    private int iconResourceId;

    public IconItem(String iconName, int iconResourceId) {
        this.iconName = iconName;
        this.iconResourceId = iconResourceId;
    }

    public String getIconName() {
        return iconName;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    @Override
    public String toString() {
        return iconName;
    }
}