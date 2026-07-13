package com.ryan.yowg.services;

public interface HostCommunicator {
    void openSSHTerminal(String address, String user, int port);
    void openPingTerminal(String address);
    void openUrl(String url);
    boolean ping(String address);
}
