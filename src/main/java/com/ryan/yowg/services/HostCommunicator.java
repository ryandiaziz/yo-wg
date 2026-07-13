package com.ryan.yowg.services;

public interface HostCommunicator {
    void openSSHTerminal(String address, String user, int port, String credentialType, String credentialSecret);
    void openPingTerminal(String address);
    void openUrl(String url);
    boolean ping(String address);
}
