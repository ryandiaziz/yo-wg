package com.ryan.yowg.components;

import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.models.Access;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.concurrent.CompletableFuture;

public class ListItemAccessComp extends HBox {
    private final Access access;
    public ListItemAccessComp(Access access) {
        this.access = access;

        Label nameLabel = new Label(access.getName());
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");

        // Tambahkan CSS agar nameLabel memiliki spasi di antara komponen
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Tambahkan event listener ke tombol Delete
        deleteButton.setOnAction(event -> {
            CompletableFuture.runAsync(() -> {
                AccessDAO.deleteAccess(access.getId());
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