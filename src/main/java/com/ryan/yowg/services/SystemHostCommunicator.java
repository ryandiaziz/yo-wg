package com.ryan.yowg.services;

import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.CredentialDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Credential;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SystemHostCommunicator implements HostCommunicator {

    @Override
    public boolean isSshpassInstalled() {
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

    @Override
    public CompletableFuture<Void> deploySharedKeyAsync(Access access, String password) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!isSshpassInstalled()) {
                    throw new IllegalStateException("The command 'sshpass' is required to deploy the key automatically.\n\nPlease install it using:\nsudo apt install sshpass");
                }

                String home = System.getProperty("user.home");
                File sshDir = new File(home + "/.ssh/yo-wg");
                if (!sshDir.exists()) {
                    sshDir.mkdirs();
                }

                String privateKeyPath = sshDir.getAbsolutePath() + "/id_yowg_shared";
                File privFile = new File(privateKeyPath);
                File pubFile = new File(privateKeyPath + ".pub");

                Credential sharedCred = null;
                List<Credential> allCreds = CredentialDAO.getAllCredentials();
                for (Credential c : allCreds) {
                    if ("Shared Yo-WG Key".equals(c.getName())) {
                        sharedCred = c;
                        break;
                    }
                }

                if (!privFile.exists() || !pubFile.exists() || sharedCred == null) {
                    if (privFile.exists()) privFile.delete();
                    if (pubFile.exists()) pubFile.delete();

                    String[] keygenCmd = {
                        "ssh-keygen", "-t", "ed25519",
                        "-f", privateKeyPath,
                        "-N", "",
                        "-q"
                    };
                    Process keygenProc = Runtime.getRuntime().exec(keygenCmd);
                    if (keygenProc.waitFor() != 0) {
                        throw new RuntimeException("Local keypair generation using ssh-keygen failed.");
                    }

                    if (sharedCred == null) {
                        sharedCred = new Credential("Shared Yo-WG Key", "", "key", privateKeyPath);
                        CredentialDAO.insertCredential(sharedCred);

                        allCreds = CredentialDAO.getAllCredentials();
                        for (Credential c : allCreds) {
                            if ("Shared Yo-WG Key".equals(c.getName())) {
                                sharedCred = c;
                                break;
                            }
                        }
                    }
                }

                String pubKeyContent = new String(Files.readAllBytes(pubFile.toPath())).trim();
                String remoteSetupCmd = "mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo '" + pubKeyContent + "' >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys";
                String[] sshCmd = {
                    "sshpass", "-p", password,
                    "ssh", "-p", String.valueOf(access.getSshPort()),
                    "-o", "StrictHostKeyChecking=no",
                    "-o", "ConnectTimeout=10",
                    access.getSshUser() + "@" + access.getAddress(),
                    remoteSetupCmd
                };

                Process sshProc = Runtime.getRuntime().exec(sshCmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(sshProc.getErrorStream()));
                StringBuilder errOutput = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errOutput.append(line).append("\n");
                }

                int exitCode = sshProc.waitFor();
                if (exitCode == 0) {
                    access.setCredentialId(sharedCred.getId());
                    AccessDAO.updateAccess(access);
                } else {
                    String errMsg = errOutput.toString().trim();
                    if (errMsg.isEmpty()) {
                        errMsg = "Unable to connect or authenticate. Please check password and server connectivity.";
                    }
                    throw new RuntimeException(errMsg);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> generateAndDeployKeyAsync(String profileName, String address, String username, int port, String password) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!isSshpassInstalled()) {
                    throw new IllegalStateException("The command 'sshpass' is required to deploy the key automatically.\n\nPlease install it using:\nsudo apt install sshpass");
                }

                String home = System.getProperty("user.home");
                File sshDir = new File(home + "/.ssh/yo-wg");
                if (!sshDir.exists()) {
                    sshDir.mkdirs();
                }

                String keyName = profileName.replaceAll("[^a-zA-Z0-9_]", "_");
                String privateKeyPath = sshDir.getAbsolutePath() + "/id_yowg_" + keyName;

                File privFile = new File(privateKeyPath);
                File pubFile = new File(privateKeyPath + ".pub");
                if (privFile.exists()) privFile.delete();
                if (pubFile.exists()) pubFile.delete();

                String[] keygenCmd = {
                    "ssh-keygen", "-t", "ed25519",
                    "-f", privateKeyPath,
                    "-N", "",
                    "-q"
                };

                Process keygenProc = Runtime.getRuntime().exec(keygenCmd);
                if (keygenProc.waitFor() != 0) {
                    throw new RuntimeException("Local keypair generation using ssh-keygen failed.");
                }

                if (!pubFile.exists()) {
                    throw new RuntimeException("Public key file was not created successfully.");
                }
                String pubKeyContent = new String(Files.readAllBytes(pubFile.toPath())).trim();

                String remoteSetupCmd = "mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo '" + pubKeyContent + "' >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys";
                String[] sshCmd = {
                    "sshpass", "-p", password,
                    "ssh", "-p", String.valueOf(port),
                    "-o", "StrictHostKeyChecking=no",
                    "-o", "ConnectTimeout=10",
                    username + "@" + address,
                    remoteSetupCmd
                };

                Process sshProc = Runtime.getRuntime().exec(sshCmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(sshProc.getErrorStream()));
                StringBuilder errOutput = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errOutput.append(line).append("\n");
                }

                int exitCode = sshProc.waitFor();
                if (exitCode == 0) {
                    Credential cred = new Credential(profileName, username, "key", privateKeyPath);
                    CredentialDAO.insertCredential(cred);
                } else {
                    String errMsg = errOutput.toString().trim();
                    if (errMsg.isEmpty()) {
                        errMsg = "Unable to connect or authenticate. Please check the address, username, or password.";
                    }
                    throw new RuntimeException(errMsg);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }
}

