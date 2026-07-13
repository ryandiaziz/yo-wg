package com.ryan.yowg.services;

import java.io.IOException;

public class SystemHostCommunicator implements HostCommunicator {

    @Override
    public void openSSHTerminal(String address, String user, int port) {
        try {
            String sshCommand = "ssh -p " + port + " " + user + "@" + address;
            String[] cmd = { "gnome-terminal", "--", "bash", "-c", sshCommand + "; exec bash" };
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            System.err.println("Failed to open SSH terminal: " + e.getMessage());
        }
    }

    @Override
    public void openPingTerminal(String address) {
        try {
            String pingCommand = "ping " + address;
            String[] cmd = { "gnome-terminal", "--", "bash", "-c", pingCommand + "; exec bash" };
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            System.err.println("Failed to open Ping terminal: " + e.getMessage());
        }
    }

    @Override
    public void openUrl(String url) {
        try {
            ProcessBuilder builder = new ProcessBuilder("xdg-open", url);
            builder.start();
        } catch (IOException e) {
            System.err.println("Failed to open URL: " + e.getMessage());
        }
    }

    @Override
    public boolean ping(String address) {
        try {
            ProcessBuilder builder = new ProcessBuilder("ping", "-c", "1", "-W", "1", address);
            Process process = builder.start();
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
