package com.ryan.yowg.services;

import com.ryan.yowg.dao.SettingsDAO;
import java.io.*;

public class SystemTunnelManager implements TunnelManager {

    private void runCommand(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[command output] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Command exited with code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void up(String name) {
        wgAction("up", name);
    }

    @Override
    public void down(String name) {
        wgAction("down", name);
    }

    private void wgAction(String action, String wgName) {
        String command = "echo " + SettingsDAO.getSetting("sudo_password") + " | sudo -S wg-quick " + action + " " + wgName;

        System.out.println(command);
        String[] cmd = { "/bin/bash", "-c", command };
        StringBuilder output = new StringBuilder();
        Process process;

        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String createConfig(String name, String content) {
        File dir = new File("/etc/wireguard");
        if (!dir.exists() || !dir.isDirectory()) {
            return "Error: Directory /etc/wireguard does not exist or is not accessible.";
        }

        File file = new File("/tmp", name + ".conf");
        String moveCommand = "echo " + SettingsDAO.getSetting("sudo_password") + " | sudo -S mv /tmp/" + name
                + ".conf /etc/wireguard/";
        System.out.println(moveCommand);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            System.out.println("File " + file.getAbsolutePath() + " created successfully!");
            runCommand(moveCommand);

            String chmodCommand = "echo " + SettingsDAO.getSetting("sudo_password") + " | sudo -S chmod 600 /etc/wireguard/" + name
                    + ".conf";

            runCommand(chmodCommand);
            System.out.println("Permissions set to 600 for " + file.getAbsolutePath());

            return "File " + file.getAbsolutePath() + " created successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating file: " + e.getMessage();
        }
    }

    @Override
    public String deleteConfig(String name) {
        File dir = new File("/etc/wireguard");
        if (!dir.exists() || !dir.isDirectory()) {
            return "Error: Directory /etc/wireguard does not exist or is not accessible.";
        }

        File file = new File(dir, name + ".conf");
        if (!file.exists()) {
            System.out.println("Java File.exists() returned false for " + file.getAbsolutePath()
                    + ", possibly due to permissions. Attempting sudo deletion...");
        }

        try {
            String command = "echo " + SettingsDAO.getSetting("sudo_password") + " | sudo -S rm " + file.getAbsolutePath();
            System.out.println("Executing command: " + command);

            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
            System.out.println("Command output: " + output);

            if (file.exists()) {
                System.out.println("File still exists after executing command.");
                return "Error: File " + file.getAbsolutePath() + " could not be deleted.";
            }

            return "File " + file.getAbsolutePath() + " deleted successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting file: " + e.getMessage();
        }
    }
}
