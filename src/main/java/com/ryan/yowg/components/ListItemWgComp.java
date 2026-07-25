package com.ryan.yowg.components;

import com.ryan.yowg.services.TunnelManager;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.dao.WireguardDAO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javafx.scene.shape.SVGPath;

public class ListItemWgComp extends HBox {

    public ListItemWgComp(Wireguard wireguard, Consumer<Wireguard> onEdit, TunnelManager tunnelManager) {
        Label nameLabel = new Label(wireguard.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-fg-default; -fx-font-size: 13px;");

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
        editButton.setOnAction(e -> onEdit.accept(wireguard));

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

        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        deleteButton.setOnAction(event -> {
            String result = tunnelManager.deleteConfig(wireguard.getName());
            System.out.println(result);

            if (result.contains("deleted successfully")) {
                CompletableFuture.runAsync(() -> {
                    WireguardDAO.deleteWireguardById(wireguard.getId());
                });
                javafx.application.Platform.runLater(() -> {
                    if (this.getParent() instanceof javafx.scene.layout.Pane) {
                        ((javafx.scene.layout.Pane) this.getParent()).getChildren().remove(this);
                    }
                });
            }
        });

        HBox actionsBox = new HBox(4, editButton, deleteButton);
        actionsBox.setStyle("-fx-alignment: center-right;");
        actionsBox.setOpacity(0.35);

        final String defaultStyle = "-fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-default; -fx-alignment: center-left;";
        final String hoverStyle = "-fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-subtle; -fx-alignment: center-left;";

        this.setOnMouseEntered(e -> {
            this.setStyle(hoverStyle);
            actionsBox.setOpacity(1.0);
        });
        this.setOnMouseExited(e -> {
            this.setStyle(defaultStyle);
            actionsBox.setOpacity(0.35);
        });

        this.getChildren().addAll(nameLabel, actionsBox);
        this.setSpacing(10);
        this.setPadding(new Insets(6, 12, 6, 12));
        this.setStyle(defaultStyle);
    }
}