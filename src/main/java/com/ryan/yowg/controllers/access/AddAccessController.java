package com.ryan.yowg.controllers.access;

import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Wireguard;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AddAccessController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField sshUserField;
    @FXML
    private TextField sshPortField;
    @FXML
    private ComboBox<Wireguard> wireguardComboBox;
    @FXML
    private ComboBox<com.ryan.yowg.models.Credential> credentialComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        saveButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(this::handleCancel);

        // Load data wireguards ke ComboBox
        loadWireguards();
        loadCredentials();
    }

    private void loadCredentials() {
        List<com.ryan.yowg.models.Credential> credentials = com.ryan.yowg.dao.CredentialDAO.getAllCredentials();
        ObservableList<com.ryan.yowg.models.Credential> credentialOptions = FXCollections.observableArrayList(credentials);
        credentialComboBox.setItems(credentialOptions);

        credentialComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(com.ryan.yowg.models.Credential cred) {
                return cred != null ? cred.getName() + " (" + cred.getUsername() + ")" : "";
            }

            @Override
            public com.ryan.yowg.models.Credential fromString(String string) {
                return credentialOptions.stream()
                        .filter(c -> (c.getName() + " (" + c.getUsername() + ")").equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadWireguards() {
        List<Wireguard> wireguards = WireguardDAO.getAllWireguards();
        ObservableList<Wireguard> wireguardOptions = FXCollections.observableArrayList(wireguards);
        wireguardComboBox.setItems(wireguardOptions);

        // Set converter untuk menampilkan nama Wireguard di ComboBox
        wireguardComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Wireguard wireguard) {
                return wireguard != null ? wireguard.getName() : "";
            }

            @Override
            public Wireguard fromString(String string) {
                return wireguardOptions.stream()
                        .filter(w -> w.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    public void handleSubmit(ActionEvent event) {
        String name = nameField.getText();
        String address = addressField.getText();
        String sshUser = sshUserField.getText().isEmpty() ? "root" : sshUserField.getText();
        int sshPort = 22;
        try {
            if (!sshPortField.getText().isEmpty()) {
                sshPort = Integer.parseInt(sshPortField.getText());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number");
            return;
        }

        Wireguard selectedWireguard = wireguardComboBox.getValue();
        com.ryan.yowg.models.Credential selectedCredential = credentialComboBox.getValue();
        Integer credentialId = selectedCredential != null ? selectedCredential.getId() : null;

        // Validasi input
        if (name.isEmpty() || address.isEmpty() || selectedWireguard == null) {
            System.out.println("Name, Address, and Wireguard must not be empty!");
            return;
        }

        final int portFinal = sshPort;
        final String userFinal = sshUser;

        CompletableFuture.runAsync(() -> {
            // Using constructor: id, name, address, sshUser, sshPort, wireguardId, credentialId
            Access newAccess = new Access(0, name, address, userFinal, portFinal, selectedWireguard.getId(), credentialId);
            AccessDAO.insertAccess(newAccess);

            Platform.runLater(() -> handleCancel(event));
        });
    }

    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
