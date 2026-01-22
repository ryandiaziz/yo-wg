package com.ryan.yowg.controllers.wireguard;

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

public class EditWgController {
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

    private Wireguard wireguard;

    public void setWireguard(Wireguard wireguard) {
        this.wireguard = wireguard;
        nameField.setText(wireguard.getName());
        noteField.setText(wireguard.getNote());
        contentField.setText(wireguard.getContent());
    }

    @FXML
    public void initialize() {
        saveButton.setText("Update");
        saveButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(this::handleCancel);
    }

    public void handleSubmit(ActionEvent event) {
        String name = nameField.getText();
        String note = noteField.getText();
        String content = contentField.getText();

        // Validasi input
        if (name.isEmpty() || note.isEmpty() || content.isEmpty()) {
            System.out.println("Name, Note and Content must not be empty!");
            return;
        }

        String oldName = wireguard.getName();

        // Update model
        wireguard.setName(name);
        wireguard.setNote(note);
        wireguard.setContent(content);

        // Update physical file
        // Assuming createConfFile overwrites. If name changes, we might need to delete
        // old file?
        // For now assuming name change is just rewriting file with new name content.
        // If name changes, we should probably delete the old file if we want to be
        // clean,
        // but typically mapped by ID or name. If name is vital for filename, we check.
        // Execute.createConfFile uses 'name' for filename.

        String result = Execute.createConfFile(name, content);
        // Logic: if name changed, we might have an old file orphan.
        // But for this MVP fix, just ensuring new file is written is key.

        System.out.println(result);
        if (result.contains("successfully")) {
            // Delete old file if name changed
            if (!oldName.equals(name)) {
                String deleteResult = Execute.deleteConfFile(oldName);
                System.out.println("Renamed: " + deleteResult);
            }

            CompletableFuture.runAsync(() -> {
                WireguardDAO.updateWireguard(wireguard);
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                System.out.println(throwable.getMessage());
                return null;
            });
        }
        handleCancel(event);
    }

    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
