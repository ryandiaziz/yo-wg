package com.ryan.yowg.controllers.access;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.ListItemAccessComp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.models.Access;
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

public class AccessController implements Initializable {
    private final MainApp mainApp;
    @FXML
    private VBox listAccessContainer;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField tfSearch;

    private List<Access> allAccessList = new ArrayList<>();

    public AccessController(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListAccess();
        btnHome.setOnAction(this::handleHome);
        btnAdd.setOnAction(this::handleAdd);
        
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList(newValue);
        });
    }

    private void handleHome(ActionEvent event) {
        mainApp.showMainPage();
    }

    private void handleAdd(ActionEvent event) {
        mainApp.showAddAccessPage();
    }

    private void setListAccess() {
        CompletableFuture.runAsync(() -> {
            allAccessList = AccessDAO.getAllAccess();

            Platform.runLater(() -> {
                updateListContainer(allAccessList);
            });
        });
    }

    private void updateListContainer(List<Access> list) {
        listAccessContainer.getChildren().clear();
        if (list.isEmpty()) {
            listAccessContainer.getChildren().add(new Label("Akses tidak ditemukan"));
        } else {
            for (Access access : list) {
                listAccessContainer.getChildren().add(
                        new ListItemAccessComp(access, mainApp::showEditAccessPage));
            }
        }
    }

    private void filterList(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            updateListContainer(allAccessList);
            return;
        }
        
        String lowerCaseKeyword = keyword.toLowerCase();
        List<Access> filteredList = allAccessList.stream()
                .filter(access -> (access.getName() != null && access.getName().toLowerCase().contains(lowerCaseKeyword)) ||
                                  (access.getAddress() != null && access.getAddress().toLowerCase().contains(lowerCaseKeyword)))
                .collect(Collectors.toList());
                
        updateListContainer(filteredList);
    }
}
