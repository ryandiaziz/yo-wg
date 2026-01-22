package com.ryan.yowg.models;

public class Access {
    private int id;
    private String name;
    private String address;
    private String sshUser = "root";
    private int sshPort = 22;
    private int wireguardId;

    public Access(String name, String address, int wireguardId) {
        this.name = name;
        this.address = address;
        this.wireguardId = wireguardId;
    }

    public Access(int id, String name, String address, int wireguardId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.wireguardId = wireguardId;
    }

    // Constructor with SSH details
    public Access(int id, String name, String address, String sshUser, int sshPort, int wireguardId) {
        this.id = id;
        this.name = name;
        this.address = address;
        if (sshUser != null)
            this.sshUser = sshUser;
        if (sshPort > 0)
            this.sshPort = sshPort;
        this.wireguardId = wireguardId;
    }

    // Getter and Setter methods
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public int getSshPort() {
        return sshPort;
    }

    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
    }

    public int getWireguardId() {
        return wireguardId;
    }

    public void setWireguardId(int wireguardId) {
        this.wireguardId = wireguardId;
    }
}
