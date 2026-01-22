package com.ryan.yowg.controllers.resource;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.ListItemResourceComp;
import com.ryan.yowg.dao.ResourceDAO;
import com.ryan.yowg.models.Resource;
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

public class ResourceController implements Initializable {
    private final MainApp mainApp;
    @FXML
    private VBox listResourceContainer;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnAddResource;

    public ResourceController(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setListResource();
        btnHome.setOnAction(this::handleHome);
        btnAddResource.setOnAction(this::handleAdd);
    }

    private void handleHome(ActionEvent event){
        mainApp.showMainPage();
    }

    private void handleAdd(ActionEvent event){
        System.out.println("TAMBAHKAN RESOUC");
        mainApp.showAddResourcePage();
    }

    private void setListResource(){
        CompletableFuture.runAsync(() -> {
            List<Resource> resources = ResourceDAO.getAllResources();

            Platform.runLater(() -> {
                int num = 1;
                for (Resource resource : resources){
                    listResourceContainer.getChildren().add(
                            new ListItemResourceComp(resource)
                    );
                }
            });
        });
    }
}
