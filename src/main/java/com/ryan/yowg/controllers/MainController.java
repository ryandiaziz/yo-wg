package com.ryan.yowg.controllers;

import com.ryan.yowg.components.AccessComp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.utils.Execute;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Toggle;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MainController implements Initializable {
    @FXML
    private VBox listWGContainer;
    @FXML
    private TextArea wgDetailInfo;
    @FXML
    private VBox accessContainer;
    @FXML
    private TextField searchField;

    static public final ToggleGroup toggleGroup = new ToggleGroup();
    static private ChangeListener<Toggle> groupListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeWireguardList();

        // Add listener only once
        addToggleGroupListener();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterWireguardList(newValue);
        });
    }

    private void filterWireguardList(String query) {
        CompletableFuture.runAsync(() -> {
            List<Wireguard> wireguards;
            if (query == null || query.trim().isEmpty()) {
                wireguards = WireguardDAO.getAllWireguards();
            } else {
                wireguards = WireguardDAO.findWireguardsByAccessName(query);
            }
            Platform.runLater(() -> populateWireguardList(wireguards));
        });
    }

    // Inisialisasi daftar Wireguard
    private void initializeWireguardList() {
        CompletableFuture.runAsync(() -> {
            List<Wireguard> wireguards = WireguardDAO.getAllWireguards();
            Platform.runLater(() -> populateWireguardList(wireguards));
        });
    }

    // Mengisi daftar RadioButton untuk Wireguard
    private void populateWireguardList(List<Wireguard> wireguards) {
        listWGContainer.getChildren().clear();
        for (Wireguard wireguard : wireguards) {
            RadioButton radioButton = createWireguardRadioButton(wireguard);
            listWGContainer.getChildren().add(radioButton);
        }
    }

    // Membuat RadioButton untuk setiap Wireguard
    private RadioButton createWireguardRadioButton(Wireguard wireguard) {
        RadioButton radioButton = new RadioButton(wireguard.getName());
        radioButton.setPadding(new Insets(2));
        radioButton.setToggleGroup(toggleGroup);
        return radioButton;
    }

    // Menambahkan listener pada ToggleGroup
    private void addToggleGroupListener() {
        if (groupListener != null) {
            toggleGroup.selectedToggleProperty().removeListener(groupListener);
        }

        groupListener = (observable, oldToggle, newToggle) -> {
            clearWireguardDetails();

            if (oldToggle != null) {
                handleWireguardToggle((RadioButton) oldToggle, "down");
            }
            if (newToggle != null) {
                handleWireguardToggle((RadioButton) newToggle, "up");
            }
        };

        toggleGroup.selectedToggleProperty().addListener(groupListener);
    }

    // Menangani aksi pada perubahan pilihan Wireguard
    private void handleWireguardToggle(RadioButton radioButton, String action) {
        String wireguardName = radioButton.getText();
        Execute.wgAction(action, wireguardName);

        if ("up".equals(action)) {
            loadWireguardDetails(wireguardName);
        }
    }

    // Memuat detail Wireguard dan daftar akses terkait
    private void loadWireguardDetails(String wireguardName) {
        CompletableFuture.runAsync(() -> {
            Wireguard wireguard = WireguardDAO.findWireguardByName(wireguardName);
            if (wireguard == null)
                return;

            List<Access> accessList = AccessDAO.getAccessByWireguard(wireguard.getId());
            Platform.runLater(() -> {
                wgDetailInfo.setText(wireguard.getContent());
                updateAccessContainer(accessList);
            });
        });
    }

    // Mengupdate kontainer akses
    private void updateAccessContainer(List<Access> accessList) {
        List<AccessComp> accessCompList = new ArrayList<>();
        for (int i = 0; i < accessList.size(); i++) {
            Access access = accessList.get(i);
            accessCompList.add(new AccessComp(i + 1, access));
        }
        accessContainer.getChildren().setAll(accessCompList);
    }

    // Menghapus detail dan daftar akses Wireguard
    private void clearWireguardDetails() {
        wgDetailInfo.clear();
        accessContainer.getChildren().clear();
    }
}
