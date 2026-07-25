package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.dao.SettingsDAO;
import com.ryan.yowg.services.TunnelManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class RootController implements Initializable {
    private final MainApp mainApp;
    private final TunnelManager tunnelManager;

    @FXML
    private VBox sidebar;
    @FXML
    private HBox topControlsBox;
    @FXML
    private VBox brandBox;
    @FXML
    private Button btnCollapse;
    @FXML
    private SVGPath collapseIcon;
    @FXML
    private Separator sep1;
    @FXML
    private Separator sep2;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnWireguards;
    @FXML
    private Button btnAccess;
    @FXML
    private Button btnResources;
    @FXML
    private Button btnCredentials;
    @FXML
    private Button btnThemeToggle;
    @FXML
    private VBox activeWgCard;
    @FXML
    private Label lblActiveHeader;
    @FXML
    private HBox activeWgStatusBox;
    @FXML
    private SVGPath statusDot;
    @FXML
    private Label activeWgLabel;
    @FXML
    private Button btnQuickDisconnect;

    private boolean isCollapsed = false;

    public RootController(MainApp mainApp, TunnelManager tunnelManager) {
        this.mainApp = mainApp;
        this.tunnelManager = tunnelManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnDashboard.setOnAction(e -> mainApp.showMainPage());
        btnWireguards.setOnAction(e -> mainApp.showWireguardMenuPage());
        btnAccess.setOnAction(e -> mainApp.showAccessMenuPage());
        btnResources.setOnAction(e -> mainApp.showResourceMenuPage());
        btnCredentials.setOnAction(e -> mainApp.showCredentialMenuPage());
        btnThemeToggle.setOnAction(this::handleThemeToggle);
        btnQuickDisconnect.setOnAction(this::handleQuickDisconnect);
        btnCollapse.setOnAction(e -> toggleSidebar());

        // Setup hover effect on all sidebar buttons/icons
        setupHoverEffect(btnCollapse);
        setupHoverEffect(btnDashboard);
        setupHoverEffect(btnWireguards);
        setupHoverEffect(btnAccess);
        setupHoverEffect(btnResources);
        setupHoverEffect(btnCredentials);
        setupHoverEffect(btnThemeToggle);

        // Restore persisted sidebar collapsed state
        String savedState = SettingsDAO.getSetting("sidebar_collapsed");
        if ("true".equals(savedState)) {
            isCollapsed = true;
            Platform.runLater(this::applySidebarLayout);
        }

        // Set initial states
        updateThemeToggleBtn();

        // Listen to tunnel manager state changes
        tunnelManager.addStateChangeListener((tunnelName, isActive) -> updateTunnelStatus(tunnelName, isActive));

        // Initial state sync
        updateTunnelStatus(tunnelManager.getActiveTunnelName(), tunnelManager.getActiveTunnelName() != null);
    }

    private void setupHoverEffect(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-cursor: hand; -fx-background-color: -color-bg-default; -fx-border-radius: 6; -fx-background-radius: 6;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-cursor: hand;"));
    }

    private void toggleSidebar() {
        isCollapsed = !isCollapsed;
        CompletableFuture.runAsync(() -> {
            SettingsDAO.saveSetting("sidebar_collapsed", String.valueOf(isCollapsed));
        });
        applySidebarLayout();
    }

    private void applySidebarLayout() {
        if (isCollapsed) {
            // Collapse sidebar
            sidebar.setPrefWidth(70);
            sidebar.setMinWidth(70);
            sidebar.setMaxWidth(70);
            sidebar.setPadding(new Insets(15, 8, 20, 8));

            topControlsBox.setAlignment(Pos.CENTER);

            brandBox.setVisible(false);
            brandBox.setManaged(false);

            btnDashboard.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnWireguards.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnAccess.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnResources.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnCredentials.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnThemeToggle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            btnDashboard.setAlignment(Pos.CENTER);
            btnWireguards.setAlignment(Pos.CENTER);
            btnAccess.setAlignment(Pos.CENTER);
            btnResources.setAlignment(Pos.CENTER);
            btnCredentials.setAlignment(Pos.CENTER);
            btnThemeToggle.setAlignment(Pos.CENTER);

            lblActiveHeader.setVisible(false);
            lblActiveHeader.setManaged(false);
            activeWgLabel.setVisible(false);
            activeWgLabel.setManaged(false);
            activeWgStatusBox.setAlignment(Pos.CENTER);

            activeWgCard.setPadding(new Insets(6, 4, 6, 4));
            activeWgCard.setPrefWidth(54);
            activeWgCard.setMaxWidth(54);
            activeWgCard.setAlignment(Pos.CENTER);

            btnQuickDisconnect.setPrefWidth(36);
            btnQuickDisconnect.setMaxWidth(36);

            collapseIcon.setContent("M8.59 16.59L10 18l6-6-6-6-1.41 1.41L13.17 12z"); // Expand right caret
        } else {
            // Expand sidebar
            sidebar.setPrefWidth(250);
            sidebar.setMinWidth(250);
            sidebar.setMaxWidth(250);
            sidebar.setPadding(new Insets(15, 12, 20, 12));

            topControlsBox.setAlignment(Pos.CENTER_LEFT);

            brandBox.setVisible(true);
            brandBox.setManaged(true);

            btnDashboard.setContentDisplay(ContentDisplay.LEFT);
            btnWireguards.setContentDisplay(ContentDisplay.LEFT);
            btnAccess.setContentDisplay(ContentDisplay.LEFT);
            btnResources.setContentDisplay(ContentDisplay.LEFT);
            btnCredentials.setContentDisplay(ContentDisplay.LEFT);
            btnThemeToggle.setContentDisplay(ContentDisplay.LEFT);

            btnDashboard.setAlignment(Pos.BASELINE_LEFT);
            btnWireguards.setAlignment(Pos.BASELINE_LEFT);
            btnAccess.setAlignment(Pos.BASELINE_LEFT);
            btnResources.setAlignment(Pos.BASELINE_LEFT);
            btnCredentials.setAlignment(Pos.BASELINE_LEFT);
            btnThemeToggle.setAlignment(Pos.BASELINE_LEFT);

            lblActiveHeader.setVisible(true);
            lblActiveHeader.setManaged(true);
            activeWgLabel.setVisible(true);
            activeWgLabel.setManaged(true);
            activeWgStatusBox.setAlignment(Pos.CENTER_LEFT);

            activeWgCard.setPadding(new Insets(10));
            activeWgCard.setPrefWidth(Region.USE_COMPUTED_SIZE);
            activeWgCard.setMaxWidth(Double.MAX_VALUE);
            activeWgCard.setAlignment(Pos.TOP_LEFT);

            btnQuickDisconnect.setPrefWidth(Region.USE_COMPUTED_SIZE);
            btnQuickDisconnect.setMaxWidth(Double.MAX_VALUE);

            collapseIcon.setContent("M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"); // Collapse left caret
        }

        updateTunnelStatus(tunnelManager.getActiveTunnelName(), tunnelManager.getActiveTunnelName() != null);
    }

    private void handleThemeToggle(ActionEvent event) {
        mainApp.toggleTheme();
        updateThemeToggleBtn();
    }

    private void updateThemeToggleBtn() {
        if (mainApp.isDarkMode()) {
            btnThemeToggle.setText("Toggle Light");
        } else {
            btnThemeToggle.setText("Toggle Dark");
        }
    }

    private void updateTunnelStatus(String tunnelName, boolean isActive) {
        Platform.runLater(() -> {
            activeWgCard.setVisible(true);
            activeWgCard.setManaged(true);

            if (isActive && tunnelName != null) {
                statusDot.setStyle("-fx-fill: -color-success-fg;");
                activeWgLabel.setText(tunnelName);

                if (isCollapsed) {
                    btnQuickDisconnect.setText("");
                    SVGPath powerIcon = new SVGPath();
                    powerIcon.setContent("M13 3h-2v10h2V3zm4.83 2.17l-1.42 1.42C17.99 7.86 19 9.81 19 12c0 3.87-3.13 7-7 7s-7-3.13-7-7c0-2.19 1.01-4.14 2.58-5.42L6.17 5.17C4.23 6.82 3 9.26 3 12c0 4.97 4.03 9 9 9s9-4.03 9-9c0-2.74-1.23-5.18-3.17-6.83z");
                    powerIcon.setScaleX(0.7);
                    powerIcon.setScaleY(0.7);
                    powerIcon.setStyle("-fx-fill: white;");
                    btnQuickDisconnect.setGraphic(powerIcon);
                    btnQuickDisconnect.setTooltip(new Tooltip("Active: " + tunnelName + " (Click to Disconnect)"));
                } else {
                    btnQuickDisconnect.setGraphic(null);
                    btnQuickDisconnect.setText("Disconnect");
                    btnQuickDisconnect.setTooltip(new Tooltip("Disconnect " + tunnelName));
                }

                btnQuickDisconnect.setVisible(true);
                btnQuickDisconnect.setManaged(true);
            } else {
                statusDot.setStyle("-fx-fill: -color-danger-fg;");
                activeWgLabel.setText("Disconnected");
                btnQuickDisconnect.setVisible(false);
                btnQuickDisconnect.setManaged(false);

                if (isCollapsed) {
                    Tooltip.install(activeWgCard, new Tooltip("No Active Tunnel"));
                }
            }
        });
    }

    private void handleQuickDisconnect(ActionEvent event) {
        String activeWg = tunnelManager.getActiveTunnelName();
        if (activeWg != null) {
            tunnelManager.down(activeWg);
        }
    }
}
