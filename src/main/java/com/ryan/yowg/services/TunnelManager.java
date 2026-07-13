package com.ryan.yowg.services;

public interface TunnelManager {
    void up(String name);
    void down(String name);
    String createConfig(String name, String content);
    String deleteConfig(String name);
}
