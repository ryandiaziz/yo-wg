package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.ListItemAccessComp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.models.Access;
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

public class AccessController implements Initializable {
    private final MainApp mainApp;
    @FXML
    private VBox listAccessContainer;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnAdd;

    public AccessController(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListAccess();
        btnHome.setOnAction(this::handleHome);
        btnAdd.setOnAction(this::handleAdd);
    }

    private void handleHome(ActionEvent event){
        mainApp.showMainPage();
    }

    private void handleAdd(ActionEvent event){
        mainApp.showAddAccessPage();
    }

    private void setListAccess(){
        CompletableFuture.runAsync(() -> {
            List<Access> accessList = AccessDAO.getAllAccess();

            Platform.runLater(() -> {
                int num = 1;
                for (Access access : accessList){
                    listAccessContainer.getChildren().add(
                            new ListItemAccessComp(access)
                    );
                }
            });
        });
    }
}
