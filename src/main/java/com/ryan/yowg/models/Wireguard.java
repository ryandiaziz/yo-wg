package com.ryan.yowg.models;

public class Wireguard {
    private int id;
    private String name;
    private String note;
    private String content;

    public Wireguard(int id, String name, String email, String content) {
        this.id = id;
        this.name = name;
        this.note = email;
        this.content = content;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getNote() { return note; }
    public String getContent() {return content;}
}