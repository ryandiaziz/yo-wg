package com.ryan.yowg.controllers;

import com.ryan.yowg.services.HostCommunicator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GenerateKeyController implements Initializable {

    private final HostCommunicator hostCommunicator;

    public GenerateKeyController(HostCommunicator hostCommunicator) {
        this.hostCommunicator = hostCommunicator;
    }

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
        lblStatus.setText("Generating key pair & deploying...");
        lblStatus.setStyle("-fx-text-fill: -color-accent-fg;");

        hostCommunicator.generateAndDeployKeyAsync(name.trim(), address.trim(), username, port, password)
                .thenRun(() -> Platform.runLater(() -> {
                    lblStatus.setText("Success! Key generated & deployed.");
                    lblStatus.setStyle("-fx-text-fill: -color-success-fg;");

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText("SSH Key Setup Completed");
                    successAlert.setContentText("Key pair generated successfully and registered on target server.\n\nProfile: " + name.trim());
                    successAlert.showAndWait();
                    closeWindow();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        lblStatus.setText("Deployment failed.");
                        lblStatus.setStyle("-fx-text-fill: -color-danger-fg;");
                        btnGenerate.setDisable(false);
                        btnCancel.setDisable(false);

                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Deployment Failed");
                        errorAlert.setHeaderText("Failed to deploy public key");
                        errorAlert.setContentText("Error details:\n" + ex.getCause().getMessage());
                        errorAlert.showAndWait();
                    });
                    return null;
                });
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}

