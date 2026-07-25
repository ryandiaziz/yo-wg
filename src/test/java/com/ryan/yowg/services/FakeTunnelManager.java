package com.ryan.yowg.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeTunnelManager implements TunnelManager {
    public final List<String> upCalls = new ArrayList<>();
    public final List<String> downCalls = new ArrayList<>();
    public final Map<String, String> configs = new HashMap<>();
    public String lastUp = null;

    private final List<TunnelStateListener> listeners = new ArrayList<>();

    @Override
    public void up(String name) {
        upCalls.add(name);
        lastUp = name;
        notifyListeners(lastUp, true);
    }

    @Override
    public void down(String name) {
        downCalls.add(name);
        if (name.equals(lastUp)) {
            lastUp = null;
        }
        notifyListeners(lastUp, lastUp != null);
    }

    @Override
    public String getActiveTunnelName() {
        return lastUp;
    }

    @Override
    public boolean isTunnelActive(String name) {
        return name != null && name.equals(lastUp);
    }

    @Override
    public void addStateChangeListener(TunnelStateListener listener) {
        if (listener != null) listeners.add(listener);
    }

    @Override
    public void removeStateChangeListener(TunnelStateListener listener) {
        if (listener != null) listeners.remove(listener);
    }

    private void notifyListeners(String name, boolean isActive) {
        for (TunnelStateListener listener : listeners) {
            listener.onTunnelStateChanged(name, isActive);
        }
    }

    @Override
    public String createConfig(String name, String content) {
        configs.put(name, content);
        return "File /etc/wireguard/" + name + ".conf created successfully!";
    }

    @Override
    public String deleteConfig(String name) {
        if (configs.containsKey(name)) {
            configs.remove(name);
            return "File /etc/wireguard/" + name + ".conf deleted successfully!";
        }
        return "Error: File does not exist.";
    }
}
