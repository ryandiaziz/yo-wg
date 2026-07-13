package com.ryan.yowg.controllers;

import com.ryan.yowg.dao.CredentialDAO;
import com.ryan.yowg.models.Credential;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class GenerateKeyController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField portField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnGenerate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCancel.setOnAction(e -> closeWindow());
        btnGenerate.setOnAction(e -> handleGenerate());
    }

    private boolean isSshpassInstalled() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"which", "sshpass"});
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleGenerate() {
        String name = nameField.getText();
        String address = addressField.getText();
        String username = usernameField.getText().trim().isEmpty() ? "root" : usernameField.getText().trim();
        String portStr = portField.getText().trim().isEmpty() ? "22" : portField.getText().trim();
        String password = passwordField.getText();

        if (name == null || name.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Profile Name, Host Address, and Temporary Password are required!");
            alert.showAndWait();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Port must be a valid number!");
            alert.showAndWait();
            return;
        }

        btnGenerate.setDisable(true);
        btnCancel.setDisable(true);
        lblStatus.setText("Checking system requirements...");
        lblStatus.setStyle("-fx-text-fill: -color-accent-fg;");

        final String finalName = name.trim();
        final String finalAddress = address.trim();
        final String finalUsername = username;
        final int finalPort = port;
        final String finalPassword = password;

        CompletableFuture.runAsync(() -> {
            try {
                // Step 1: Check sshpass
                if (!isSshpassInstalled()) {
                    Platform.runLater(() -> {
                        lblStatus.setText("Error: sshpass not installed.");
                        lblStatus.setStyle("-fx-text-fill: -color-danger-fg;");
                        btnGenerate.setDisable(false);
                        btnCancel.setDisable(false);

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("sshpass Missing");
                        alert.setHeaderText("sshpass is required");
                        alert.setContentText("The command 'sshpass' is required to deploy the key automatically.\n\nPlease install it using:\nsudo apt install sshpass");
                        alert.showAndWait();
                    });
                    return;
                }

                // Step 2: Ensure directories exist
                String home = System.getProperty("user.home");
                File sshDir = new File(home + "/.ssh/yo-wg");
                if (!sshDir.exists()) {
                    sshDir.mkdirs();
                }

                // Step 3: Run ssh-keygen
                String keyName = finalName.replaceAll("[^a-zA-Z0-9_]", "_");
                String privateKeyPath = sshDir.getAbsolutePath() + "/id_yowg_" + keyName;
                
                File privFile = new File(privateKeyPath);
                File pubFile = new File(privateKeyPath + ".pub");
                if (privFile.exists()) privFile.delete();
                if (pubFile.exists()) pubFile.delete();

                Platform.runLater(() -> lblStatus.setText("Generating key pair locally..."));

                String[] keygenCmd = {
                    "ssh-keygen", "-t", "ed25519",
                    "-f", privateKeyPath,
                    "-N", "", // No passphrase
                    "-q"      // Quiet mode
                };

                Process keygenProc = Runtime.getRuntime().exec(keygenCmd);
                if (keygenProc.waitFor() != 0) {
                    throw new Exception("Local keypair generation using ssh-keygen failed.");
                }

                // Step 4: Read public key content
                if (!pubFile.exists()) {
                    throw new Exception("Public key file was not created successfully.");
                }
                String pubKeyContent = new String(Files.readAllBytes(pubFile.toPath())).trim();

                // Step 5: Pushing key to remote server using sshpass
                Platform.runLater(() -> lblStatus.setText("Connecting to server & deploying key..."));

                String remoteSetupCmd = "mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo '" + pubKeyContent + "' >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys";
                String[] sshCmd = {
                    "sshpass", "-p", finalPassword,
                    "ssh", "-p", String.valueOf(finalPort),
                    "-o", "StrictHostKeyChecking=no",
                    "-o", "ConnectTimeout=10",
                    finalUsername + "@" + finalAddress,
                    remoteSetupCmd
                };

                Process sshProc = Runtime.getRuntime().exec(sshCmd);

                // Capture errors from stderr
                BufferedReader reader = new BufferedReader(new InputStreamReader(sshProc.getErrorStream()));
                StringBuilder errOutput = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errOutput.append(line).append("\n");
                }

                int exitCode = sshProc.waitFor();
                if (exitCode == 0) {
                    // Success: insert into database
                    Credential cred = new Credential(finalName, finalUsername, "key", privateKeyPath);
                    CredentialDAO.insertCredential(cred);

                    Platform.runLater(() -> {
                        lblStatus.setText("Success! Key generated & deployed.");
                        lblStatus.setStyle("-fx-text-fill: -color-success-fg;");

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("SSH Key Setup Completed");
                        successAlert.setContentText("Key pair generated successfully and registered on target server.\n\nProfile: " + finalName + "\nPath: " + privateKeyPath);
                        successAlert.showAndWait();
                        closeWindow();
                    });
                } else {
                    String errMsg = errOutput.toString().trim();
                    if (errMsg.isEmpty()) {
                        errMsg = "Unable to connect or authenticate. Please check the address, username, or password.";
                    }
                    throw new Exception(errMsg);
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblStatus.setText("Deployment failed.");
                    lblStatus.setStyle("-fx-text-fill: -color-danger-fg;");
                    btnGenerate.setDisable(false);
                    btnCancel.setDisable(false);

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deployment Failed");
                    errorAlert.setHeaderText("Failed to deploy public key");
                    errorAlert.setContentText("Error details:\n" + e.getMessage());
                    errorAlert.showAndWait();
                });
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
