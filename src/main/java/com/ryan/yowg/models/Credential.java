package com.ryan.yowg.models;

public class Credential {
    private int id;
    private String name;
    private String username;
    private String type; // "password" | "key"
    private String secret; // password plaintext or ssh private key path

    public Credential(String name, String username, String type, String secret) {
        this.name = name;
        this.username = username;
        this.type = type;
        this.secret = secret;
    }

    public Credential(int id, String name, String username, String type, String secret) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.type = type;
        this.secret = secret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return name + " (" + username + ")";
    }
}
