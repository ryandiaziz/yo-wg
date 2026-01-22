package com.ryan.yowg.components;

import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.utils.Execute;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.concurrent.CompletableFuture;

import java.util.function.Consumer;

public class ListItemWgComp extends HBox {

    public ListItemWgComp(Wireguard wireguard, Consumer<Wireguard> onEdit) {
        Label nameLabel = new Label(wireguard.getName());
        Button deleteButton = new Button("Delete");
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> onEdit.accept(wireguard));

        // Tambahkan CSS agar nameLabel memiliki spasi di antara komponen
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Tambahkan event listener ke tombol Delete
        deleteButton.setOnAction(event -> {
            String result = Execute.deleteConfFile(wireguard.getName());
            System.out.println(result);

            // Hapus dari database jika file berhasil dihapus
            if (result.contains("deleted successfully")) {
                CompletableFuture.runAsync(() -> {
                    WireguardDAO.deleteWireguardById(wireguard.getId());
                });
            }
        });

        // Tambahkan elemen ke dalam HBox
        this.getChildren().addAll(nameLabel, editButton, deleteButton);

        // Atur jarak antar elemen
        this.setSpacing(10);

        // Tambahkan CSS tambahan untuk tata letak
        this.setStyle("-fx-alignment: center-left; -fx-padding: 5;"); // Mengatur elemen rata kiri dan padding
    }
}