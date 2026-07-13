package com.ryan.yowg.components;

import com.ryan.yowg.models.Credential;
import com.ryan.yowg.dao.CredentialDAO;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.shape.SVGPath;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ListItemCredentialComp extends HBox {

    public ListItemCredentialComp(Credential credential, Consumer<Credential> onEdit, Runnable onDeletedRefresh) {
        VBox labelBox = new VBox(2);
        Label nameLabel = new Label(credential.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-fg-default; -fx-font-size: 13px;");

        String typeStr = credential.getType().equals("key") ? "SSH Key Path" : "Password";
        Label infoLabel = new Label(credential.getUsername() + " | Type: " + typeStr);
        infoLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 11px;");

        labelBox.getChildren().addAll(nameLabel, infoLabel);
        labelBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        // Edit Icon (Pencil)
        SVGPath editIcon = new SVGPath();
        editIcon.setContent(
                "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
        editIcon.setScaleX(0.7);
        editIcon.setScaleY(0.7);
        editIcon.setStyle("-fx-fill: -color-accent-fg;");
        Button editButton = new Button();
        editButton.setGraphic(editIcon);
        editButton.getStyleClass().add("flat");
        editButton.setCursor(javafx.scene.Cursor.HAND);
        editButton.setOnAction(e -> onEdit.accept(credential));

        // Delete Icon (Trash)
        SVGPath deleteIcon = new SVGPath();
        deleteIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        deleteIcon.setScaleX(0.7);
        deleteIcon.setScaleY(0.7);
        deleteIcon.setStyle("-fx-fill: -color-danger-fg;");
        Button deleteButton = new Button();
        deleteButton.setGraphic(deleteIcon);
        deleteButton.getStyleClass().add("flat");
        deleteButton.setCursor(javafx.scene.Cursor.HAND);

        deleteButton.setOnAction(event -> {
            CompletableFuture.runAsync(() -> {
                CredentialDAO.deleteCredential(credential.getId());
                Platform.runLater(() -> {
                    if (this.getParent() instanceof javafx.scene.layout.Pane) {
                        ((javafx.scene.layout.Pane) this.getParent()).getChildren().remove(this);
                    }
                    if (onDeletedRefresh != null) {
                        onDeletedRefresh.run();
                    }
                });
            });
        });

        this.getChildren().addAll(labelBox, editButton, deleteButton);
        this.setSpacing(10);
        this.setPadding(new Insets(8, 12, 8, 12));
        this.setStyle("-fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-default; -fx-alignment: center-left;");
    }
}
