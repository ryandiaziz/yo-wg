package com.ryan.yowg.controllers.resource;

import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.ResourceDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Resource;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditResourceController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField urlField;
    @FXML
    private ComboBox<Access> accessComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label titleLabel; // Assuming we can change title or add a label if needed, but for now reuse
                              // fields

    private Resource resource;

    public void setResource(Resource resource) {
        this.resource = resource;
        nameField.setText(resource.getName());
        urlField.setText(resource.getUrl());

        // Select the current access
        if (accessComboBox.getItems() != null) {
            for (Access access : accessComboBox.getItems()) {
                if (access.getId() == resource.getAccessId()) {
                    accessComboBox.setValue(access);
                    break;
                }
            }
        }
    }

    @FXML
    public void initialize() {
        saveButton.setText("Update"); // Change button text
        saveButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(this::handleCancel);

        // Load data wireguards ke ComboBox
        this.loadAccesses();
    }

    private void loadAccesses() {
        List<Access> accessList = AccessDAO.getAllAccess();
        ObservableList<Access> accessObservableList = FXCollections.observableArrayList(accessList);
        accessComboBox.setItems(accessObservableList);

        // Set converter untuk menampilkan nama Access di ComboBox
        accessComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Access access) {
                return access != null ? access.getName() : "";
            }

            @Override
            public Access fromString(String string) {
                return accessObservableList.stream()
                        .filter(w -> w.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    public void handleSubmit(ActionEvent event) {
        String name = nameField.getText();
        String url = urlField.getText();
        Access selectedAccess = accessComboBox.getValue();

        // Validasi input
        if (name.isEmpty() || url.isEmpty() || selectedAccess == null) {
            System.out.println("Name, Url, and Access must not be empty!");
            return;
        }

        CompletableFuture.runAsync(() -> {
            resource.setName(name);
            resource.setUrl(url);
            resource.setAccessId(selectedAccess.getId());
            ResourceDAO.updateResource(resource);

            Platform.runLater(() -> handleCancel(event));
        });
    }

    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
