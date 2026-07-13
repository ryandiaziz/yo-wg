package com.ryan.yowg.controllers.resource;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.ListItemResourceComp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.ResourceDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Resource;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ResourceController implements Initializable {
    private final MainApp mainApp;
    @FXML
    private VBox listResourceContainer;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnAddResource;
    @FXML
    private TextField tfSearch;

    private List<Access> allAccessList = new ArrayList<>();
    private List<Resource> allResourceList = new ArrayList<>();

    public ResourceController(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setListResource();
        btnHome.setOnAction(this::handleHome);
        btnAddResource.setOnAction(this::handleAdd);
        
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            updateListContainer(newValue);
        });
    }

    private void handleHome(ActionEvent event) {
        mainApp.showMainPage();
    }

    private void handleAdd(ActionEvent event) {
        System.out.println("TAMBAHKAN RESOUC");
        mainApp.showAddResourcePage();
    }

    private void setListResource() {
        CompletableFuture.runAsync(() -> {
            allAccessList = AccessDAO.getAllAccess();
            allResourceList = ResourceDAO.getAllResources();

            Platform.runLater(() -> {
                updateListContainer("");
            });
        });
    }

    private void updateListContainer(String keyword) {
        listResourceContainer.getChildren().clear();
        boolean hasAnyMatch = false;

        for (Access access : allAccessList) {
            List<Resource> matchedResources = allResourceList.stream()
                    .filter(r -> r.getAccessId() == access.getId())
                    .filter(r -> matchesSearch(r, access, keyword))
                    .collect(Collectors.toList());

            if (!matchedResources.isEmpty()) {
                hasAnyMatch = true;
                VBox groupVBox = new VBox();
                groupVBox.setSpacing(5);
                for (Resource r : matchedResources) {
                    groupVBox.getChildren().add(new ListItemResourceComp(r, mainApp::showEditResourcePage));
                }
                
                TitledPane titledPane = new TitledPane(access.getName() + " (" + access.getAddress() + ")", groupVBox);
                if (keyword != null && !keyword.trim().isEmpty()) {
                    titledPane.setExpanded(true);
                } else {
                    titledPane.setExpanded(false);
                }
                
                listResourceContainer.getChildren().add(titledPane);
            }
        }

        if (!hasAnyMatch) {
            listResourceContainer.getChildren().add(new Label("Resource tidak ditemukan"));
        }
    }

    private boolean matchesSearch(Resource r, Access a, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return true;
        String lower = keyword.toLowerCase();
        return (r.getName() != null && r.getName().toLowerCase().contains(lower)) ||
               (a.getName() != null && a.getName().toLowerCase().contains(lower)) ||
               (a.getAddress() != null && a.getAddress().toLowerCase().contains(lower));
    }
}
