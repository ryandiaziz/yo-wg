package com.ryan.yowg.controllers;

import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.utils.Execute;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

public class AddWgController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField noteField;
    @FXML
    private TextArea contentField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    public void initialize() {
        saveButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(this::handleCancel);
    }

    public void handleSubmit(ActionEvent event){
        String name = nameField.getText();
        String note = noteField.getText();
        String content = contentField.getText();

        // Validasi input
        if (name.isEmpty() || note.isEmpty() || content.isEmpty()) {
            System.out.println("Name, Note and Content must not be empty!");
            return;
        }

        String result = Execute.createConfFile(name, content);
        System.out.println(result);
        if (result.contains("successfully")) {
            CompletableFuture.runAsync(()->{
                Wireguard wireguard = new Wireguard(0, name, note, content);
                WireguardDAO.insertWireguard(wireguard);
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                System.out.println(throwable.getMessage());
                return null;
            });
        }
        handleCancel(event);
    }

    private void handleCancel(ActionEvent event){
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
