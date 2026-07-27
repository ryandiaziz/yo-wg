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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
    private Button btnClearSearch;
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
            boolean hasText = newValue != null && !newValue.trim().isEmpty();
            if (btnClearSearch != null) {
                btnClearSearch.setVisible(hasText);
                btnClearSearch.setManaged(hasText);
            }
            filterWireguardList(newValue);
        });

        if (btnClearSearch != null) {
            btnClearSearch.setOnAction(e -> {
                searchField.setText("");
                searchField.requestFocus();
            });
        }

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
            List<Wireguard> wireguards = WireguardDAO.searchWireguards(query);
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
                    boolean activeExistsInDb = wireguards.stream().anyMatch(w -> w.getName().equals(activeName));
                    if (activeExistsInDb) {
                        selectedWireguardName = activeName;
                    } else if (!wireguards.isEmpty()) {
                        selectedWireguardName = wireguards.get(0).getName();
                    } else {
                        selectedWireguardName = null;
                    }
                }
                populateWireguardList(wireguards);
                if (selectedWireguardName != null) {
                    loadWireguardDetails(selectedWireguardName);
                } else {
                    updateConnectionStateUI();
                }
            });
        });
    }

    private void populateWireguardList(List<Wireguard> wireguards) {
        listWGContainer.getChildren().clear();

        if (wireguards == null || wireguards.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setPadding(new Insets(30, 15, 30, 15));

            Label emptyLabel = new Label("No WireGuard Tunnels Found");
            emptyLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px; -fx-font-weight: bold;");

            Label subLabel = new Label("Click 'Wireguards' in sidebar to add or import a tunnel.");
            subLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 11px; -fx-text-alignment: center;");
            subLabel.setWrapText(true);

            emptyBox.getChildren().addAll(emptyLabel, subLabel);
            listWGContainer.getChildren().add(emptyBox);
            updateConnectionStateUI();
            return;
        }

        for (Wireguard wireguard : wireguards) {
            boolean isActive = tunnelManager.isTunnelActive(wireguard.getName());
            boolean isSelected = wireguard.getName().equals(selectedWireguardName);

            Label nameLabel = new Label(wireguard.getName());

            VBox labelBox = new VBox(2);
            labelBox.getChildren().add(nameLabel);

            String subText = wireguard.getTunnelAddress();
            Label subLabel = null;
            if (subText != null && !subText.trim().isEmpty()) {
                subLabel = new Label(subText);
                labelBox.getChildren().add(subLabel);
            }
            HBox.setHgrow(labelBox, Priority.ALWAYS);

            HBox graphicBox = new HBox(8);
            graphicBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            graphicBox.getChildren().add(labelBox);

            Label activeBadge = null;
            if (isActive) {
                activeBadge = new Label("● Active");
                graphicBox.getChildren().add(activeBadge);
            }

            if (isSelected) {
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white;");
                if (subLabel != null) {
                    subLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255, 255, 255, 0.85);");
                }
                if (activeBadge != null) {
                    activeBadge.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #a3e635;");
                }
            } else {
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: -color-fg-default;");
                if (subLabel != null) {
                    subLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: -color-fg-muted;");
                }
                if (activeBadge != null) {
                    activeBadge.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: -color-success-fg;");
                }
            }

            Button itemButton = new Button();
            itemButton.setGraphic(graphicBox);
            itemButton.setMaxWidth(Double.MAX_VALUE);
            itemButton.setPadding(new Insets(8, 12, 8, 12));
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
        String targetTunnel = selectedWireguardName;

        btnConnectionToggle.setDisable(true);
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(14, 14);
        spinner.setStyle("-fx-progress-color: white;");
        btnConnectionToggle.setGraphic(spinner);
        btnConnectionToggle.setText(willConnect ? "Connecting..." : "Disconnecting...");

        CompletableFuture.runAsync(() -> {
            if (willConnect) {
                tunnelManager.up(targetTunnel);
            } else {
                tunnelManager.down(targetTunnel);
            }
        }).whenComplete((res, ex) -> {
            Platform.runLater(() -> {
                btnConnectionToggle.setDisable(false);
                btnConnectionToggle.setGraphic(null);
                if (ex != null) {
                    ex.printStackTrace();
                }
                updateConnectionStateUI();
                filterWireguardList(searchField != null ? searchField.getText() : "");
            });
        });
    }

    private void loadWireguardDetails(String wireguardName) {
        showAccessLoadingState();

        CompletableFuture.runAsync(() -> {
            Wireguard wireguard = WireguardDAO.findWireguardByName(wireguardName);
            if (wireguard == null) {
                Platform.runLater(() -> {
                    updateAccessContainer(Collections.emptyList());
                    updateConnectionStateUI();
                });
                return;
            }

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

    private void showAccessLoadingState() {
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(20, 20);

        Label loadingLabel = new Label("Loading Access Servers...");
        loadingLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");

        HBox loadingBox = new HBox(8, spinner, loadingLabel);
        loadingBox.setAlignment(javafx.geometry.Pos.CENTER);
        loadingBox.setPadding(new Insets(20));

        accessContainer.getChildren().setAll(loadingBox);
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
        if (accessList == null || accessList.isEmpty()) {
            VBox emptyBox = new VBox(8);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setPadding(new Insets(30, 20, 30, 20));

            Label emptyLabel = new Label("No Access Servers Found");
            emptyLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 13px; -fx-font-weight: bold;");

            Label subLabel = new Label("Click '+ Add Access' above to register an Access Server for this tunnel.");
            subLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 11px;");

            emptyBox.getChildren().addAll(emptyLabel, subLabel);
            accessContainer.getChildren().setAll(emptyBox);
            return;
        }

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
