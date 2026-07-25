package com.ryan.yowg.controllers.access;

import com.ryan.yowg.components.SearchableComboBoxUtil;
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

public class EditAccessController {
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

    private Access access;

    public void setAccess(Access access) {
        this.access = access;
        nameField.setText(access.getName());
        addressField.setText(access.getAddress());
        sshUserField.setText(access.getSshUser());
        sshPortField.setText(String.valueOf(access.getSshPort()));

        // Select the associated wireguard
        if (wireguardComboBox.getItems() != null) {
            for (Wireguard wg : wireguardComboBox.getItems()) {
                if (wg.getId() == access.getWireguardId()) {
                    wireguardComboBox.setValue(wg);
                    break;
                }
            }
        }

        // Select the associated credential
        if (credentialComboBox.getItems() != null && access.getCredentialId() != null) {
            for (com.ryan.yowg.models.Credential cred : credentialComboBox.getItems()) {
                if (cred.getId() == access.getCredentialId()) {
                    credentialComboBox.setValue(cred);
                    break;
                }
            }
        }
    }

    @FXML
    public void initialize() {
        saveButton.setText("Update");
        saveButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(this::handleCancel);

        // Load data wireguards & credentials ke ComboBox
        loadWireguards();
        loadCredentials();
    }

    private void loadCredentials() {
        List<com.ryan.yowg.models.Credential> credentials = com.ryan.yowg.dao.CredentialDAO.getAllCredentials();
        ObservableList<com.ryan.yowg.models.Credential> credentialOptions = FXCollections.observableArrayList(credentials);

        SearchableComboBoxUtil.makeSearchable(credentialComboBox, credentialOptions, cred ->
                cred != null ? cred.getName() + " (" + cred.getUsername() + ")" : ""
        );
    }

    private void loadWireguards() {
        List<Wireguard> wireguards = WireguardDAO.getAllWireguards();
        ObservableList<Wireguard> wireguardOptions = FXCollections.observableArrayList(wireguards);

        SearchableComboBoxUtil.makeSearchable(wireguardComboBox, wireguardOptions, wg ->
                wg != null ? wg.getName() : ""
        );
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

        // Update access object
        access.setName(name);
        access.setAddress(address);
        access.setSshUser(sshUser);
        access.setSshPort(sshPort);
        access.setWireguardId(selectedWireguard.getId());
        access.setCredentialId(credentialId);

        CompletableFuture.runAsync(() -> {
            AccessDAO.updateAccess(access);
            Platform.runLater(() -> handleCancel(event));
        });
    }

    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
