package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.ListItemCredentialComp;
import com.ryan.yowg.dao.CredentialDAO;
import com.ryan.yowg.models.Credential;
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

public class CredentialController implements Initializable {
    private final MainApp mainApp;

    @FXML
    private VBox listContainer;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnAutoGenerate;
    @FXML
    private TextField searchField;

    private List<Credential> allCredentials = new ArrayList<>();

    public CredentialController(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCredentials();
        btnHome.setOnAction(this::handleHome);
        btnAdd.setOnAction(this::handleAdd);
        btnAutoGenerate.setOnAction(this::handleAutoGenerate);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList(newValue);
        });
    }

    private void handleHome(ActionEvent event) {
        mainApp.showMainPage();
    }

    private void handleAdd(ActionEvent event) {
        mainApp.showAddCredentialPage();
        loadCredentials(); // Reload after add dialog closes
    }

    private void handleAutoGenerate(ActionEvent event) {
        mainApp.showGenerateKeyPage();
        loadCredentials(); // Reload after generation dialog closes
    }

    public void loadCredentials() {
        CompletableFuture.runAsync(() -> {
            allCredentials = CredentialDAO.getAllCredentials();
            Platform.runLater(() -> {
                updateListContainer(allCredentials);
            });
        });
    }

    private void updateListContainer(List<Credential> list) {
        listContainer.getChildren().clear();
        if (list.isEmpty()) {
            listContainer.getChildren().add(new Label("No credentials found."));
        } else {
            for (Credential cred : list) {
                listContainer.getChildren().add(
                        new ListItemCredentialComp(cred, credential -> {
                            mainApp.showEditCredentialPage(credential);
                            loadCredentials(); // Reload after edit dialog closes
                        }, this::loadCredentials)
                );
            }
        }
    }

    private void filterList(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            updateListContainer(allCredentials);
            return;
        }

        String lowerCaseKeyword = keyword.toLowerCase();
        List<Credential> filteredList = allCredentials.stream()
                .filter(cred -> (cred.getName() != null && cred.getName().toLowerCase().contains(lowerCaseKeyword)) ||
                                (cred.getUsername() != null && cred.getUsername().toLowerCase().contains(lowerCaseKeyword)))
                .collect(Collectors.toList());

        updateListContainer(filteredList);
    }
}
