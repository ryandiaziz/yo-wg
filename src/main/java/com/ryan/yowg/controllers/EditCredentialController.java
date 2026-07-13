package com.ryan.yowg.controllers;

import com.ryan.yowg.dao.CredentialDAO;
import com.ryan.yowg.models.Credential;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCredentialController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField secretField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    private Credential credential;

    private static final String TYPE_PASSWORD = "Password";
    private static final String TYPE_KEY = "Private Key Path";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeComboBox.setItems(FXCollections.observableArrayList(TYPE_PASSWORD, TYPE_KEY));
        
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (TYPE_PASSWORD.equals(newVal)) {
                secretField.setPromptText("Enter SSH Password");
            } else if (TYPE_KEY.equals(newVal)) {
                secretField.setPromptText("Enter Absolute Private Key Path (e.g. /home/user/.ssh/id_rsa)");
            }
        });

        cancelButton.setOnAction(e -> closeWindow());
        saveButton.setOnAction(e -> handleSave());
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
        nameField.setText(credential.getName());
        usernameField.setText(credential.getUsername());
        
        String typeDisplay = credential.getType().equals("key") ? TYPE_KEY : TYPE_PASSWORD;
        typeComboBox.getSelectionModel().select(typeDisplay);
        secretField.setText(credential.getSecret());
    }

    private void handleSave() {
        if (credential == null) return;

        String name = nameField.getText();
        String username = usernameField.getText();
        String typeDisplay = typeComboBox.getSelectionModel().getSelectedItem();
        String secret = secretField.getText();

        if (name == null || name.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            typeDisplay == null || secret == null || secret.trim().isEmpty()) {
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required!");
            alert.showAndWait();
            return;
        }

        String typeDb = typeDisplay.equals(TYPE_KEY) ? "key" : "password";
        
        credential.setName(name.trim());
        credential.setUsername(username.trim());
        credential.setType(typeDb);
        credential.setSecret(secret.trim());

        CredentialDAO.updateCredential(credential);

        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
