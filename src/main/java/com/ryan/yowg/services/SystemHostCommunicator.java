package com.ryan.yowg.services;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.io.IOException;

public class SystemHostCommunicator implements HostCommunicator {

    private boolean isSshpassInstalled() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"which", "sshpass"});
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void openSSHTerminal(String address, String user, int port, String credentialType, String credentialSecret) {
        try {
            String sshCommand;
            if (credentialType != null && credentialType.equals("key") && credentialSecret != null && !credentialSecret.trim().isEmpty()) {
                sshCommand = "ssh -i \"" + credentialSecret + "\" -p " + port + " " + user + "@" + address;
            } else if (credentialType != null && credentialType.equals("password") && credentialSecret != null && !credentialSecret.trim().isEmpty()) {
                if (isSshpassInstalled()) {
                    sshCommand = "sshpass -p \"" + credentialSecret + "\" ssh -p " + port + " " + user + "@" + address;
                } else {
                    sshCommand = "ssh -p " + port + " " + user + "@" + address;
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("sshpass Not Found");
                        alert.setHeaderText("SSH Password Automation Warning");
                        alert.setContentText("The command 'sshpass' is not installed on your system. Automated login will not work.\n\nTo enable automated password login, please install it:\n\nsudo apt install sshpass\n\nLaunching standard SSH terminal now.");
                        alert.showAndWait();
                    });
                }
            } else {
                sshCommand = "ssh -p " + port + " " + user + "@" + address;
            }

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
