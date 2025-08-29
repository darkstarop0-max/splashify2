package com.curosoft.splashify.model;

public class Category {
    public final String id;
    public final String name;
    public final int iconResId;

    public Category(String id, String name, int iconResId) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
    }
}
