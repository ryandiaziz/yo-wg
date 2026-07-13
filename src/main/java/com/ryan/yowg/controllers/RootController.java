package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.services.TunnelManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    private final MainApp mainApp;
    private final TunnelManager tunnelManager;
    @FXML
    private MenuItem addNew;
    @FXML
    private MenuItem shutdownWg;
    @FXML
    private MenuItem shutdownAndCloseWg;
    @FXML
    private MenuItem menuAccess;
    @FXML
    private MenuItem menuResources;
    @FXML
    private MenuItem menuWireguard;

    public RootController(MainApp mainApp, TunnelManager tunnelManager) {
        this.mainApp = mainApp;
        this.tunnelManager = tunnelManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addNew.setOnAction(this::handleAddNew);
        shutdownWg.setOnAction(this::handleShutdownWg);
        shutdownAndCloseWg.setOnAction(this::handleShutdownAndCloseWg);
        menuAccess.setOnAction(this::handleMenuAccess);
        menuWireguard.setOnAction(this::handleMenuWireGuard);
        menuResources.setOnAction(this::handleMenuResource);
    }

    private void handleAddNew(ActionEvent event) {
        mainApp.showAddWgPage();
    }

    private void handleShutdownWg(ActionEvent event) {
        String activeWg = MainController.getActiveWireguardName();
        if (activeWg != null) {
            tunnelManager.down(activeWg);
            MainController.setActiveWireguardName(null);
            if (MainController.listRefresher != null) {
                MainController.listRefresher.run();
            }
        }
    }

    private void handleShutdownAndCloseWg(ActionEvent event) {
        String activeWg = MainController.getActiveWireguardName();
        if (activeWg != null) {
            tunnelManager.down(activeWg);
            MainController.setActiveWireguardName(null);
            if (MainController.listRefresher != null) {
                MainController.listRefresher.run();
            }
        }
        mainApp.getPrimaryStage().close();
    }

    private void handleMenuAccess(ActionEvent event) {
        mainApp.showAccessMenuPage();
    }

    private void handleMenuResource(ActionEvent event) {
        mainApp.showResourceMenuPage();
    }

    private void handleMenuWireGuard(ActionEvent event) {
        mainApp.showWireguardMenuPage();
    }
}
