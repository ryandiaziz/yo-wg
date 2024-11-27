package com.ryan.yowg.controllers;

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
    private ComboBox<Wireguard> wireguardComboBox;
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
        Wireguard selectedWireguard = wireguardComboBox.getValue();

        // Validasi input
        if (name.isEmpty() || address.isEmpty() || selectedWireguard == null) {
            System.out.println("Name, Note, Content, and Wireguard must not be empty!");
            return;
        }

        System.out.println(name + " " + address + " " + selectedWireguard.getName());

        CompletableFuture.runAsync(() -> {
            AccessDAO.insertAccess(new Access(name, address, selectedWireguard.getId()));

            Platform.runLater(() -> handleCancel(event));
        });
    }

    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
