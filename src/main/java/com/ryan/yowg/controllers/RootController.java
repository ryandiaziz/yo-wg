package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.utils.Execute;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;

import java.net.URL;
import java.util.ResourceBundle;

import static com.ryan.yowg.controllers.MainController.toggleGroup;

public class RootController implements Initializable {
    private final MainApp mainApp;
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

    public RootController(MainApp mainApp){
        this.mainApp = mainApp;
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

    private void handleAddNew(ActionEvent event){
        mainApp.showAddWgPage();
    }

    private void handleShutdownWg(ActionEvent event){
        if (toggleGroup.getSelectedToggle() != null){
            RadioButton selectedToggle = (RadioButton) toggleGroup.getSelectedToggle();
            Execute.wgAction("down", selectedToggle.getText());
            toggleGroup.getSelectedToggle().setSelected(false);
        }
    }

    private void handleShutdownAndCloseWg(ActionEvent event){
        if (toggleGroup.getSelectedToggle() != null){
            RadioButton selectedToggle = (RadioButton) toggleGroup.getSelectedToggle();
            Execute.wgAction("down", selectedToggle.getText());
            toggleGroup.getSelectedToggle().setSelected(false);
        }
        mainApp.getPrimaryStage().close();
    }

    private void handleMenuAccess(ActionEvent event){
        mainApp.showAccessMenuPage();
    }

    private void handleMenuResource(ActionEvent event){
        mainApp.showResourceMenuPage();
    }

    private void handleMenuWireGuard(ActionEvent event){
        mainApp.showWireguardMenuPage();
    }
}
