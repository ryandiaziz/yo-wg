package com.ryan.yowg.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeHostCommunicator implements HostCommunicator {
    public final List<String> sshCalls = new ArrayList<>();
    public final List<String> pingTerminalCalls = new ArrayList<>();
    public final List<String> urlCalls = new ArrayList<>();
    public final Map<String, Boolean> pingResponses = new HashMap<>();

    @Override
    public void openSSHTerminal(String address, String user, int port, String credentialType, String credentialSecret) {
        sshCalls.add(user + "@" + address + ":" + port + " (Type: " + credentialType + ")");
    }

    @Override
    public void openPingTerminal(String address) {
        pingTerminalCalls.add(address);
    }

    @Override
    public void openUrl(String url) {
        urlCalls.add(url);
    }

    @Override
    public boolean ping(String address) {
        return pingResponses.getOrDefault(address, true);
    }
}
