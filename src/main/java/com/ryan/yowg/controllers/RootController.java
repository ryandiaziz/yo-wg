package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.services.TunnelManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    private final MainApp mainApp;
    private final TunnelManager tunnelManager;

    @FXML
    private VBox sidebar;
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
    private SVGPath statusDot;
    @FXML
    private Label activeWgLabel;
    @FXML
    private Button btnQuickDisconnect;

    private static RootController instance;
    private boolean isCollapsed = false;

    public RootController(MainApp mainApp, TunnelManager tunnelManager) {
        this.mainApp = mainApp;
        this.tunnelManager = tunnelManager;
    }

    public static void updateActiveTunnelStatus(String tunnelName, boolean isActive) {
        if (instance != null) {
            instance.updateTunnelStatus(tunnelName, isActive);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;

        btnDashboard.setOnAction(e -> mainApp.showMainPage());
        btnWireguards.setOnAction(e -> mainApp.showWireguardMenuPage());
        btnAccess.setOnAction(e -> mainApp.showAccessMenuPage());
        btnResources.setOnAction(e -> mainApp.showResourceMenuPage());
        btnCredentials.setOnAction(e -> mainApp.showCredentialMenuPage());
        btnThemeToggle.setOnAction(this::handleThemeToggle);
        btnQuickDisconnect.setOnAction(this::handleQuickDisconnect);
        btnCollapse.setOnAction(e -> toggleSidebar());

        // Set initial states
        updateThemeToggleBtn();
        
        // Restore active tunnel status representation if any
        String activeWg = MainController.getActiveWireguardName();
        if (activeWg != null) {
            updateTunnelStatus(activeWg, true);
        } else {
            updateTunnelStatus(null, false);
        }
    }

    private void toggleSidebar() {
        if (isCollapsed) {
            // Expand sidebar
            sidebar.setPrefWidth(250);
            sidebar.setMinWidth(250);
            sidebar.setMaxWidth(250);
            sidebar.setPadding(new Insets(15, 12, 20, 12));

            brandBox.setVisible(true);
            brandBox.setManaged(true);

            btnDashboard.setContentDisplay(ContentDisplay.LEFT);
            btnWireguards.setContentDisplay(ContentDisplay.LEFT);
            btnAccess.setContentDisplay(ContentDisplay.LEFT);
            btnResources.setContentDisplay(ContentDisplay.LEFT);
            btnCredentials.setContentDisplay(ContentDisplay.LEFT);
            btnThemeToggle.setContentDisplay(ContentDisplay.LEFT);

            // Alignment resetting
            btnDashboard.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
            btnWireguards.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
            btnAccess.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
            btnResources.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
            btnCredentials.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
            btnThemeToggle.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);

            activeWgCard.setVisible(true);
            activeWgCard.setManaged(true);
            sep1.setVisible(true);
            sep1.setManaged(true);
            sep2.setVisible(true);
            sep2.setManaged(true);

            collapseIcon.setContent("M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"); // Collapse left caret
            isCollapsed = false;
        } else {
            // Collapse sidebar
            sidebar.setPrefWidth(70);
            sidebar.setMinWidth(70);
            sidebar.setMaxWidth(70);
            sidebar.setPadding(new Insets(15, 8, 20, 8));

            brandBox.setVisible(false);
            brandBox.setManaged(false);

            btnDashboard.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnWireguards.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnAccess.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnResources.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnCredentials.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnThemeToggle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            // Alignment centering
            btnDashboard.setAlignment(javafx.geometry.Pos.CENTER);
            btnWireguards.setAlignment(javafx.geometry.Pos.CENTER);
            btnAccess.setAlignment(javafx.geometry.Pos.CENTER);
            btnResources.setAlignment(javafx.geometry.Pos.CENTER);
            btnCredentials.setAlignment(javafx.geometry.Pos.CENTER);
            btnThemeToggle.setAlignment(javafx.geometry.Pos.CENTER);

            activeWgCard.setVisible(false);
            activeWgCard.setManaged(false);
            sep1.setVisible(false);
            sep1.setManaged(false);
            sep2.setVisible(false);
            sep2.setManaged(false);

            collapseIcon.setContent("M8.59 16.59L10 18l6-6-6-6-1.41 1.41L13.17 12z"); // Expand right caret
            isCollapsed = true;
        }
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
            if (isActive && tunnelName != null) {
                statusDot.setStyle("-fx-fill: -color-success-fg;");
                activeWgLabel.setText(tunnelName);
                btnQuickDisconnect.setVisible(true);
                btnQuickDisconnect.setManaged(true);
            } else {
                statusDot.setStyle("-fx-fill: -color-danger-fg;");
                activeWgLabel.setText("Disconnected");
                btnQuickDisconnect.setVisible(false);
                btnQuickDisconnect.setManaged(false);
            }
        });
    }

    private void handleQuickDisconnect(ActionEvent event) {
        String activeWg = MainController.getActiveWireguardName();
        if (activeWg != null) {
            tunnelManager.down(activeWg);
            MainController.setActiveWireguardName(null);
            updateTunnelStatus(null, false);
            if (MainController.listRefresher != null) {
                MainController.listRefresher.run();
            }
        }
    }
}
