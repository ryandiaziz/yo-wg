package com.ryan.yowg.components;

import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.ResourceDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Resource;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.function.Consumer;

import java.util.concurrent.CompletableFuture;

public class ListItemResourceComp extends HBox {
    private final Resource resource;

    public ListItemResourceComp(Resource resource, Consumer<Resource> onEdit) {
        this.resource = resource;

        Label nameLabel = new Label(resource.getName());
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> onEdit.accept(resource));

        // Tambahkan CSS agar nameLabel memiliki spasi di antara komponen
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Tambahkan event listener ke tombol Delete
        deleteButton.setOnAction(event -> {
            CompletableFuture.runAsync(() -> {
                ResourceDAO.deleteResource(resource.getId());
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

    public Resource getResource() {
        return resource;
    }
}