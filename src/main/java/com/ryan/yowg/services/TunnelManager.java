package com.ryan.yowg.services;

public interface TunnelManager {

    @FunctionalInterface
    interface TunnelStateListener {
        void onTunnelStateChanged(String activeTunnelName, boolean isActive);
    }

    void up(String name);
    void down(String name);
    String createConfig(String name, String content);
    String deleteConfig(String name);

    String getActiveTunnelName();
    boolean isTunnelActive(String name);
    void addStateChangeListener(TunnelStateListener listener);
    void removeStateChangeListener(TunnelStateListener listener);
}

