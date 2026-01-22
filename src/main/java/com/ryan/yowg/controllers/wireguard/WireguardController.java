package com.ryan.yowg.controllers.wireguard;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.ListItemWgComp;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Wireguard;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class WireguardController implements Initializable {
    private final MainApp mainApp;
    @FXML
    private VBox listWireguardContainer;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnAdd;

    public WireguardController(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListWireguard();
        btnHome.setOnAction(this::handleHome);
        btnAdd.setOnAction(this::handleAdd);
    }

    private void handleHome(ActionEvent event) {
        mainApp.showMainPage();
    }

    private void handleAdd(ActionEvent event) {
        mainApp.showAddWgPage();
    }

    private void setListWireguard() {
        CompletableFuture.runAsync(() -> {
            List<Wireguard> wireguardList = WireguardDAO.getAllWireguards();

            Platform.runLater(() -> {
                int num = 1;
                for (Wireguard wireguard : wireguardList) {
                    listWireguardContainer.getChildren().add(
                            new ListItemWgComp(wireguard, mainApp::showEditWgPage));
                }
            });
        });
    }
}
