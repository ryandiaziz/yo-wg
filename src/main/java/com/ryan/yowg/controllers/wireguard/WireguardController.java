package com.ryan.yowg.controllers.wireguard;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.ListItemWgComp;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.services.TunnelManager;
import com.ryan.yowg.services.TunnelSyncService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WireguardController implements Initializable {
    private final MainApp mainApp;
    private final TunnelManager tunnelManager;
    @FXML
    private VBox listWireguardContainer;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnSync;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField tfSearch;

    private List<Wireguard> allWireguardList = new ArrayList<>();

    public WireguardController(MainApp mainApp, TunnelManager tunnelManager) {
        this.mainApp = mainApp;
        this.tunnelManager = tunnelManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListWireguard();
        btnHome.setOnAction(this::handleHome);
        btnAdd.setOnAction(this::handleAdd);
        if (btnSync != null) {
            btnSync.setOnAction(this::handleSync);
        }
        
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList(newValue);
        });
    }

    private void handleHome(ActionEvent event) {
        mainApp.showMainPage();
    }

    private void handleAdd(ActionEvent event) {
        mainApp.showAddWgPage();
    }

    private void handleSync(ActionEvent event) {
        if (btnSync != null) {
            btnSync.setDisable(true);
            btnSync.setText("Syncing...");
        }

        CompletableFuture.runAsync(() -> {
            int count = TunnelSyncService.syncSystemTunnels();
            System.out.println("[WireguardController] Sync completed. Tunnels updated/added: " + count);

            Platform.runLater(() -> {
                setListWireguard();
                if (btnSync != null) {
                    btnSync.setDisable(false);
                    btnSync.setText("Sync System Tunnels");
                }
            });
        });
    }


    private void setListWireguard() {
        CompletableFuture.runAsync(() -> {
            allWireguardList = WireguardDAO.getAllWireguards();

            Platform.runLater(() -> {
                updateListContainer(allWireguardList);
            });
        });
    }

    private void updateListContainer(List<Wireguard> list) {
        listWireguardContainer.getChildren().clear();
        if (list.isEmpty()) {
            listWireguardContainer.getChildren().add(new Label("Wireguard tidak ditemukan"));
        } else {
            for (Wireguard wg : list) {
                listWireguardContainer.getChildren().add(
                        new ListItemWgComp(wg, mainApp::showEditWgPage, tunnelManager));
            }
        }
    }

    private void filterList(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            updateListContainer(allWireguardList);
            return;
        }
        
        String lowerCaseKeyword = keyword.toLowerCase();
        List<Wireguard> filteredList = allWireguardList.stream()
                .filter(wg -> (wg.getName() != null && wg.getName().toLowerCase().contains(lowerCaseKeyword)) ||
                              (wg.getNote() != null && wg.getNote().toLowerCase().contains(lowerCaseKeyword)))
                .collect(Collectors.toList());
                
        updateListContainer(filteredList);
    }
}
