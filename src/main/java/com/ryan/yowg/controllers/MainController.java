package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.components.AccessComp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.services.TunnelManager;
import com.ryan.yowg.services.HostCommunicator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MainController implements Initializable {
    private final MainApp mainApp;
    private final TunnelManager tunnelManager;
    private final HostCommunicator hostCommunicator;

    public MainController(MainApp mainApp, TunnelManager tunnelManager, HostCommunicator hostCommunicator) {
        this.mainApp = mainApp;
        this.tunnelManager = tunnelManager;
        this.hostCommunicator = hostCommunicator;
    }

    public MainController(TunnelManager tunnelManager, HostCommunicator hostCommunicator) {
        this(null, tunnelManager, hostCommunicator);
    }

    @FXML
    private VBox listWGContainer;
    @FXML
    private TextArea wgDetailInfo;
    @FXML
    private VBox accessContainer;
    @FXML
    private TextField searchField;
    @FXML
    private Button btnAddAccess;

    @FXML
    private VBox placeholderView;
    @FXML
    private VBox actualDetailView;
    @FXML
    private Label lblTunnelName;
    @FXML
    private Label lblStatusBadge;
    @FXML
    private ToggleButton btnConnectionToggle;

    private String selectedWireguardName = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeWireguardList();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterWireguardList(newValue);
        });

        btnConnectionToggle.setOnAction(this::handleConnectionToggle);

        if (btnAddAccess != null) {
            btnAddAccess.setOnAction(this::handleAddAccess);
        }

        tunnelManager.addStateChangeListener((activeName, isActive) -> {
            Platform.runLater(() -> {
                updateConnectionStateUI();
                filterWireguardList(searchField != null ? searchField.getText() : "");
            });
        });
    }

    private void handleAddAccess(ActionEvent event) {
        Wireguard selectedWg = null;
        if (selectedWireguardName != null) {
            selectedWg = WireguardDAO.findWireguardByName(selectedWireguardName);
        }
        if (mainApp != null) {
            mainApp.showAddAccessPage(selectedWg);
            if (selectedWireguardName != null) {
                loadWireguardDetails(selectedWireguardName);
            }
        }
    }

    private void filterWireguardList(String query) {
        CompletableFuture.runAsync(() -> {
            List<Wireguard> wireguards;
            if (query == null || query.trim().isEmpty()) {
                wireguards = WireguardDAO.getAllWireguards();
            } else {
                wireguards = WireguardDAO.findWireguardsByAccessName(query);
            }
            Platform.runLater(() -> {
                populateWireguardList(wireguards);
                if (selectedWireguardName != null) {
                    loadWireguardDetails(selectedWireguardName);
                }
            });
        });
    }

    private void initializeWireguardList() {
        CompletableFuture.runAsync(() -> {
            List<Wireguard> wireguards = WireguardDAO.getAllWireguards();
            String activeName = tunnelManager.getActiveTunnelName();
            Platform.runLater(() -> {
                if (selectedWireguardName == null && activeName != null) {
                    selectedWireguardName = activeName;
                }
                populateWireguardList(wireguards);
                if (selectedWireguardName != null) {
                    loadWireguardDetails(selectedWireguardName);
                }
            });
        });
    }

    private void populateWireguardList(List<Wireguard> wireguards) {
        listWGContainer.getChildren().clear();
        for (Wireguard wireguard : wireguards) {
            boolean isActive = tunnelManager.isTunnelActive(wireguard.getName());
            boolean isSelected = wireguard.getName().equals(selectedWireguardName);

            Label nameLabel = new Label(wireguard.getName());
            nameLabel.setStyle("-fx-font-weight: bold;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            HBox graphicBox = new HBox(8);
            graphicBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            graphicBox.getChildren().add(nameLabel);

            if (isActive) {
                Label activeBadge = new Label("● Active");
                activeBadge.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: -color-success-fg;");
                graphicBox.getChildren().add(activeBadge);
            }

            Button itemButton = new Button();
            itemButton.setGraphic(graphicBox);
            itemButton.setMaxWidth(Double.MAX_VALUE);
            itemButton.setPadding(new Insets(10, 15, 10, 15));
            itemButton.setCursor(javafx.scene.Cursor.HAND);

            updateListItemStyle(itemButton, isSelected);

            itemButton.setOnAction(e -> {
                selectedWireguardName = wireguard.getName();
                loadWireguardDetails(selectedWireguardName);
                populateWireguardList(wireguards);
            });
            listWGContainer.getChildren().add(itemButton);
        }

        updateConnectionStateUI();
    }

    private void updateListItemStyle(Button button, boolean isSelected) {
        button.getStyleClass().removeAll("flat", "accent");
        if (isSelected) {
            button.getStyleClass().addAll("accent");
        } else {
            button.getStyleClass().addAll("flat");
        }
    }

    private void handleConnectionToggle(ActionEvent event) {
        if (selectedWireguardName == null) return;

        boolean willConnect = btnConnectionToggle.isSelected();
        if (willConnect) {
            tunnelManager.up(selectedWireguardName);
        } else {
            tunnelManager.down(selectedWireguardName);
        }
        updateConnectionStateUI();
    }

    private void loadWireguardDetails(String wireguardName) {
        CompletableFuture.runAsync(() -> {
            Wireguard wireguard = WireguardDAO.findWireguardByName(wireguardName);
            if (wireguard == null)
                return;

            List<Access> accessList = AccessDAO.getAccessByWireguard(wireguard.getId());
            Platform.runLater(() -> {
                wgDetailInfo.setText(wireguard.getContent());
                
                String query = searchField.getText();
                if (query != null && !query.trim().isEmpty()) {
                    String lowerQuery = query.toLowerCase();
                    accessList.removeIf(access -> access.getName() == null || !access.getName().toLowerCase().contains(lowerQuery));
                }
                
                updateAccessContainer(accessList);
                updateConnectionStateUI();
            });
        });
    }

    private void updateConnectionStateUI() {
        if (selectedWireguardName == null) {
            placeholderView.setVisible(true);
            placeholderView.setManaged(true);
            actualDetailView.setVisible(false);
            actualDetailView.setManaged(false);
            return;
        }

        placeholderView.setVisible(false);
        placeholderView.setManaged(false);
        actualDetailView.setVisible(true);
        actualDetailView.setManaged(true);

        lblTunnelName.setText(selectedWireguardName);

        boolean isActive = tunnelManager.isTunnelActive(selectedWireguardName);
        btnConnectionToggle.setSelected(isActive);

        btnConnectionToggle.getStyleClass().removeAll("danger", "accent");
        if (isActive) {
            btnConnectionToggle.setText("Disconnect");
            btnConnectionToggle.getStyleClass().add("danger");
            btnConnectionToggle.setStyle("-fx-background-color: -color-danger-emphasis; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            lblStatusBadge.setText("Connected");
            lblStatusBadge.setStyle("-fx-text-fill: -color-success-fg;");
        } else {
            btnConnectionToggle.setText("Connect");
            btnConnectionToggle.getStyleClass().add("accent");
            btnConnectionToggle.setStyle("-fx-background-color: -color-accent-emphasis; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            lblStatusBadge.setText("Disconnected");
            lblStatusBadge.setStyle("-fx-text-fill: -color-danger-fg;");
        }
    }

    private void updateAccessContainer(List<Access> accessList) {
        List<AccessComp> accessCompList = new ArrayList<>();
        for (int i = 0; i < accessList.size(); i++) {
            Access access = accessList.get(i);
            AccessComp comp = new AccessComp(i + 1, access, hostCommunicator, mainApp, () -> {
                if (selectedWireguardName != null) {
                    loadWireguardDetails(selectedWireguardName);
                }
            });
            accessCompList.add(comp);
        }
        accessContainer.getChildren().setAll(accessCompList);
    }
}
