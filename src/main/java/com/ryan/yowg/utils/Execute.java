package com.ryan.yowg.utils;

import java.io.*;

public class Execute {
    public static void wgAction(String action, String wgName) {
        String command = "echo "+ ReadConfig.getPassword() +" | sudo -S wg-quick " + action + " " + wgName;

        System.out.println(command);
        String[] cmd = {"/bin/bash", "-c", command};
        StringBuilder output = new StringBuilder();
        Process process;

        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void command(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // Baca output dari proses
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

    /**
     * Creates a WireGuard configuration file in /etc/wireguard with proper permissions.
     * @param fileName The name of the file (without .conf extension).
     * @param content The content to write into the file.
     * @return A success or error message.
     */
    public static String createConfFile(String fileName, String content) {
        // Path to WireGuard configuration directory
        File dir = new File("/etc/wireguard");
        if (!dir.exists() || !dir.isDirectory()) {
            return "Error: Directory /etc/wireguard does not exist or is not accessible.";
        }

        File file = new File("/tmp", fileName + ".conf");
        String moveCommand = "echo " + ReadConfig.getPassword() + " | sudo -S mv /tmp/" + fileName + ".conf /etc/wireguard/";
        System.out.println(moveCommand);


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write content to the file
            writer.write(content);
            System.out.println("File " + file.getAbsolutePath() + " created successfully!");
            Execute.command(moveCommand);

            // Set permissions to 600 using chmod with password
            String chmodCommand = "echo " + ReadConfig.getPassword() + " | sudo -S chmod 600 /etc/wireguard/" + fileName + ".conf";

            Execute.command(chmodCommand);
            System.out.println("Permissions set to 600 for " + file.getAbsolutePath());

            return "File " + file.getAbsolutePath() + " created successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error creating file: " + e.getMessage();
        }
    }

    public static String deleteConfFile(String fileName) {
        File dir = new File("/etc/wireguard");
        if (!dir.exists() || !dir.isDirectory()) {
            return "Error: Directory /etc/wireguard does not exist or is not accessible.";
        }

        File file = new File(dir, fileName + ".conf");
        if (!file.exists()) {
            return "Error: File " + file.getAbsolutePath() + " does not exist.";
        }

        try {
            // Command to delete the file using sudo
            String command = "echo " + ReadConfig.getPassword() + " | sudo -S rm " + file.getAbsolutePath();
            System.out.println("Executing command: " + command);

            ProcessBuilder builder = new ProcessBuilder(
                    "/bin/bash", "-c", command
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // Read process output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
            System.out.println("Command output: " + output);

            // Check if file still exists
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