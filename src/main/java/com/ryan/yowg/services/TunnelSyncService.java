package com.ryan.yowg.services;

import com.ryan.yowg.dao.SettingsDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Wireguard;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class TunnelSyncService {

    /**
     * Runs auto-sync only if it hasn't been completed on first run yet.
     */
    public static void syncIfFirstRun() {
        String isCompleted = SettingsDAO.getSetting("initial_sync_completed");
        if (!"true".equalsIgnoreCase(isCompleted)) {
            syncSystemTunnels();
            SettingsDAO.saveSetting("initial_sync_completed", "true");
        }
    }

    /**
     * Scans /etc/wireguard/*.conf and syncs them with the local database.
     * @return Number of new or updated tunnels.
     */
    public static int syncSystemTunnels() {
        Map<String, String> systemConfigs = scanWireguardConfigs();
        int changedCount = 0;

        for (Map.Entry<String, String> entry : systemConfigs.entrySet()) {
            String name = entry.getKey();
            String content = entry.getValue();

            Wireguard existing = WireguardDAO.findWireguardByName(name);
            if (existing == null) {
                Wireguard newWg = new Wireguard(0, name, "Imported from /etc/wireguard", content);
                WireguardDAO.insertWireguard(newWg);
                changedCount++;
                System.out.println("[TunnelSyncService] Imported new tunnel: " + name);
            } else {
                String existingContent = existing.getContent() != null ? existing.getContent().trim() : "";
                String newContent = content != null ? content.trim() : "";

                if (!existingContent.equals(newContent)) {
                    existing.setContent(content);
                    WireguardDAO.updateWireguard(existing);
                    changedCount++;
                    System.out.println("[TunnelSyncService] Updated existing tunnel content: " + name);
                }
            }
        }

        return changedCount;
    }

    private static Map<String, String> scanWireguardConfigs() {
        Map<String, String> configs = new HashMap<>();
        File dir = new File("/etc/wireguard");

        File[] files = null;
        if (dir.exists() && dir.isDirectory()) {
            files = dir.listFiles((d, name) -> name.endsWith(".conf"));
        }

        if (files != null && files.length > 0) {
            // Direct Java File I/O readable
            for (File f : files) {
                String name = f.getName().substring(0, f.getName().length() - 5);
                try {
                    String content = Files.readString(f.toPath());
                    configs.put(name, content);
                } catch (Exception e) {
                    // Fallback to sudo if direct read fails
                    String content = readConfigWithSudo(f.getName());
                    if (content != null) {
                        configs.put(name, content);
                    }
                }
            }
        } else {
            // Fallback: Use sudo ls to find configs if directory listing failed due to permissions
            String sudoPassword = SettingsDAO.getSetting("sudo_password");
            if (sudoPassword != null && !sudoPassword.trim().isEmpty()) {
                try {
                    String command = "echo " + sudoPassword + " | sudo -S ls /etc/wireguard";
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
                    Process process = builder.start();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.endsWith(".conf")) {
                                String name = line.substring(0, line.length() - 5);
                                String content = readConfigWithSudo(line);
                                if (content != null) {
                                    configs.put(name, content);
                                }
                            }
                        }
                    }
                    process.waitFor();
                } catch (Exception e) {
                    System.err.println("[TunnelSyncService] Error scanning /etc/wireguard via sudo: " + e.getMessage());
                }
            }
        }

        return configs;
    }

    private static String readConfigWithSudo(String fileName) {
        String sudoPassword = SettingsDAO.getSetting("sudo_password");
        if (sudoPassword == null || sudoPassword.trim().isEmpty()) {
            return null;
        }

        try {
            String command = "echo " + sudoPassword + " | sudo -S cat /etc/wireguard/" + fileName;
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
            Process process = builder.start();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0 && sb.length() > 0) {
                return sb.toString();
            }
        } catch (Exception e) {
            System.err.println("[TunnelSyncService] Error reading " + fileName + " with sudo: " + e.getMessage());
        }
        return null;
    }
}
