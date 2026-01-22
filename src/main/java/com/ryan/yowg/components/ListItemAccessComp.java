package com.ryan.yowg.components;

import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.models.Access;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.concurrent.CompletableFuture;

import java.util.function.Consumer;

import javafx.scene.shape.SVGPath;

public class ListItemAccessComp extends HBox {
    private final Access access;

    public ListItemAccessComp(Access access, Consumer<Access> onEdit) {
        this.access = access;

        Label nameLabel = new Label(access.getName());

        // Edit Icon (Pencil)
        SVGPath editIcon = new SVGPath();
        editIcon.setContent(
                "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
        editIcon.setScaleX(0.7);
        editIcon.setScaleY(0.7);
        Button editButton = new Button();
        editButton.setGraphic(editIcon);
        editButton.setOnAction(e -> onEdit.accept(access));

        // Delete Icon (Trash)
        SVGPath deleteIcon = new SVGPath();
        deleteIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        deleteIcon.setScaleX(0.7);
        deleteIcon.setScaleY(0.7);
        Button deleteButton = new Button();
        deleteButton.setGraphic(deleteIcon);

        // Tambahkan CSS agar nameLabel memiliki spasi di antara komponen
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Tambahkan event listener ke tombol Delete
        deleteButton.setOnAction(event -> {
            CompletableFuture.runAsync(() -> {
                AccessDAO.deleteAccess(access.getId());
                javafx.application.Platform.runLater(() -> {
                    if (this.getParent() instanceof javafx.scene.layout.Pane) {
                        ((javafx.scene.layout.Pane) this.getParent()).getChildren().remove(this);
                    }
                });
            });
        });

        // Tambahkan elemen ke dalam HBox
        this.getChildren().addAll(nameLabel, editButton, deleteButton);

        // Atur jarak antar elemen
        this.setSpacing(10);

        // Tambahkan CSS tambahan untuk tata letak
        this.setStyle("-fx-alignment: center-left; -fx-padding: 5;"); // Mengatur elemen rata kiri dan padding
    }

    public Access getAccess() {
        return access;
    }
}