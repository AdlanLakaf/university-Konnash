package com.konnash.models;

public class Client {

    private long   id;
    private String name;
    private String phone;
    private String address;
    private long   createdAt;

    // Default constructor
    public Client() {}

    public Client(String name, String phone, String address) {
        this.name      = name;
        this.phone     = phone;
        this.address   = address;
        this.createdAt = System.currentTimeMillis();
    }

    // ─── Getters & Setters ────────────────────────────────────

    public long getId()                     { return id; }
    public void setId(long id)              { this.id = id; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    public String getPhone()                { return phone; }
    public void setPhone(String phone)      { this.phone = phone; }

    public String getAddress()              { return address; }
    public void setAddress(String address)  { this.address = address; }

    public long getCreatedAt()              { return createdAt; }
    public void setCreatedAt(long createdAt){ this.createdAt = createdAt; }
}
