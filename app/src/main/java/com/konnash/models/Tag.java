package com.konnash.models;

public class Tag {

    private long   id;
    private String name;
    private String color;   // hex color string e.g. "#7BAED6"
    private long   createdAt;

    // Default constructor
    public Tag() {}

    public Tag(String name, String color) {
        this.name      = name;
        this.color     = color;
        this.createdAt = System.currentTimeMillis();
    }

    // ─── Getters & Setters ────────────────────────────────────

    public long getId()                     { return id; }
    public void setId(long id)              { this.id = id; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    public String getColor()                { return color; }
    public void setColor(String color)      { this.color = color; }

    public long getCreatedAt()              { return createdAt; }
    public void setCreatedAt(long createdAt){ this.createdAt = createdAt; }
}
