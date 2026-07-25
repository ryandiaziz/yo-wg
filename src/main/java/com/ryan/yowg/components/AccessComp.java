package com.ryan.yowg.components;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.CredentialDAO;
import com.ryan.yowg.dao.ResourceDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Credential;
import com.ryan.yowg.models.Resource;
import com.ryan.yowg.services.HostCommunicator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.util.List;
import java.util.Optional;

public class AccessComp extends HBox {
    private final HostCommunicator hostCommunicator;
    private final MainApp mainApp;
    private final Runnable onUpdateCallback;

    public AccessComp(int num, Access access, HostCommunicator hostCommunicator) {
        this(num, access, hostCommunicator, null, null);
    }

    public AccessComp(int num, Access access, HostCommunicator hostCommunicator, MainApp mainApp, Runnable onUpdateCallback) {
        this.hostCommunicator = hostCommunicator;
        this.mainApp = mainApp;
        this.onUpdateCallback = onUpdateCallback;

        Label numLabel = new Label(String.valueOf(num));
        numLabel.setMinWidth(25);
        numLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-weight: bold; -fx-font-size: 13px;");

        Label nameLabel = new Label(access.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: -color-fg-default;");

        TextField addressField = new TextField(access.getAddress());
        addressField.setEditable(false);
        addressField.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");

        VBox contentBox = new VBox(nameLabel, addressField);
        contentBox.setSpacing(2);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        // SSH Icon (Terminal) - Primary Action
        SVGPath sshIcon = new SVGPath();
        sshIcon.setContent(
                "M20,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V6C22,4.89 21.1,4 20,4M20,18H4V6H20V18M7.5,15L9,16.5L13.5,12L9,7.5L7.5,9L10.5,12L7.5,15Z");
        sshIcon.setScaleX(0.7);
        sshIcon.setScaleY(0.7);
        sshIcon.setStyle("-fx-fill: -color-fg-default;");
        Button sshButton = new Button();
        sshButton.setGraphic(sshIcon);
        sshButton.getStyleClass().add("flat");
        sshButton.setCursor(javafx.scene.Cursor.HAND);
        sshButton.setTooltip(new Tooltip("SSH"));
        sshButton.setOnAction(e -> {
            String credType = null;
            String credSecret = null;
            String user = access.getSshUser();
            if (access.getCredentialId() != null) {
                Credential credential = CredentialDAO.getCredentialById(access.getCredentialId());
                if (credential != null) {
                    credType = credential.getType();
                    credSecret = credential.getSecret();
                    if (credential.getUsername() != null && !credential.getUsername().trim().isEmpty()) {
                        user = credential.getUsername();
                    }
                }
            }
            hostCommunicator.openSSHTerminal(access.getAddress(), user, access.getSshPort(), credType, credSecret);
        });

        // Ping Terminal Icon (Pulse/Activity) - Primary Action
        SVGPath pingIcon = new SVGPath();
        pingIcon.setContent(
                "M3,13H5.79L6.08,13.03L9,20L15,4L17.92,12.97L18.21,13H21V15H17.21L15,19L9,3L6.08,18.97L5.79,19H3V13Z");
        pingIcon.setScaleX(0.7);
        pingIcon.setScaleY(0.7);
        pingIcon.setStyle("-fx-fill: -color-fg-default;");
        Button pingButton = new Button();
        pingButton.setGraphic(pingIcon);
        pingButton.getStyleClass().add("flat");
        pingButton.setCursor(javafx.scene.Cursor.HAND);
        pingButton.setTooltip(new Tooltip("Ping Terminal"));
        pingButton.setOnAction(e -> hostCommunicator.openPingTerminal(access.getAddress()));

        // Resources Icon (List/Folder) - Primary Action
        SVGPath resourcesIcon = new SVGPath();
        resourcesIcon.setContent("M4 14h4v-4H4v4zm0 5h4v-4H4v4zM4 9h4V5H4v4zm5 5h12v-4H9v4zm0 5h12v-4H9v4zM9 5v4h12V5H9z");
        resourcesIcon.setScaleX(0.7);
        resourcesIcon.setScaleY(0.7);
        resourcesIcon.setStyle("-fx-fill: -color-fg-default;");
        Button resourcesButton = new Button();
        resourcesButton.setGraphic(resourcesIcon);
        resourcesButton.getStyleClass().add("flat");
        resourcesButton.setCursor(javafx.scene.Cursor.HAND);
        resourcesButton.setTooltip(new Tooltip("Resources"));
        resourcesButton.setOnAction(e -> showResourcesDialog(access));

        // Key/Autologin Icon (Key) - Primary Action
        SVGPath keyIcon = new SVGPath();
        keyIcon.setContent("M12.65 10C11.83 7.67 9.61 6 7 6c-3.31 0-6 2.69-6 6s2.69 6 6 6c2.61 0 4.83-1.67 5.65-4H17v4h4v-4h2v-4H12.65zM7 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z");
        keyIcon.setScaleX(0.7);
        keyIcon.setScaleY(0.7);

        Button keyButton = new Button();
        keyButton.setGraphic(keyIcon);
        keyButton.getStyleClass().add("flat");
        keyButton.setCursor(javafx.scene.Cursor.HAND);

        // Helper to update style dynamically
        Runnable updateKeyButtonStyle = () -> {
            if (access.getCredentialId() != null) {
                keyIcon.setStyle("-fx-fill: -color-success-fg;");
                keyButton.setTooltip(new Tooltip("SSH Key Autologin Active"));
            } else {
                keyIcon.setStyle("-fx-fill: -color-fg-muted;");
                keyButton.setTooltip(new Tooltip("Setup SSH Key Autologin"));
            }
        };
        updateKeyButtonStyle.run();

        keyButton.setOnAction(e -> {
            if (access.getCredentialId() != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("SSH Key Autologin");
                alert.setHeaderText("SSH Key autologin is active for " + access.getName());
                alert.setContentText("Choose an action below:");

                ButtonType btnRemove = new ButtonType("Disable Autologin");
                ButtonType btnRedeploy = new ButtonType("Redeploy Key");
                ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(btnRemove, btnRedeploy, btnCancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnRemove) {
                    access.setCredentialId(null);
                    AccessDAO.updateAccess(access);
                    updateKeyButtonStyle.run();
                } else if (result.isPresent() && result.get() == btnRedeploy) {
                    showDeployKeyDialog(access, updateKeyButtonStyle);
                }
            } else {
                showDeployKeyDialog(access, updateKeyButtonStyle);
            }
        });

        // Edit Button (Pencil Icon) - Secondary Action (semi-transparent by default, full on hover)
        SVGPath editIcon = new SVGPath();
        editIcon.setContent("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
        editIcon.setScaleX(0.7);
        editIcon.setScaleY(0.7);
        editIcon.setStyle("-fx-fill: -color-fg-muted;");
        Button editButton = new Button();
        editButton.setGraphic(editIcon);
        editButton.getStyleClass().add("flat");
        editButton.setCursor(javafx.scene.Cursor.HAND);
        editButton.setTooltip(new Tooltip("Edit Access Server (Right-click card for options)"));
        editButton.setOpacity(0.35);
        editButton.setOnAction(e -> {
            if (this.mainApp != null) {
                this.mainApp.showEditAccessPage(access);
                if (this.onUpdateCallback != null) {
                    this.onUpdateCallback.run();
                }
            }
        });

        // Primary actions always visible
        HBox primaryActionsBox = new HBox(4, sshButton, pingButton, resourcesButton, keyButton);
        primaryActionsBox.setStyle("-fx-alignment: center-right;");

        // Hover highlight on card + full opacity on edit button
        final String baseStyle = "-fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-default; -fx-alignment: center-left;";
        final String hoverStyle = "-fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-subtle; -fx-alignment: center-left;";

        this.setOnMouseEntered(e -> {
            this.setStyle(hoverStyle);
            editButton.setOpacity(1.0);
        });
        this.setOnMouseExited(e -> {
            this.setStyle(baseStyle);
            editButton.setOpacity(0.35);
        });

        // Context Menu (Right-click on card)
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Edit Access Server");
        editMenuItem.setOnAction(e -> {
            if (this.mainApp != null) {
                this.mainApp.showEditAccessPage(access);
                if (this.onUpdateCallback != null) {
                    this.onUpdateCallback.run();
                }
            }
        });

        MenuItem deleteMenuItem = new MenuItem("Delete Access Server");
        deleteMenuItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Access Server");
            alert.setHeaderText("Delete " + access.getName() + "?");
            alert.setContentText("Are you sure you want to delete this Access Server?");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    AccessDAO.deleteAccess(access.getId());
                    if (this.onUpdateCallback != null) {
                        this.onUpdateCallback.run();
                    }
                }
            });
        });
        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
        this.setOnContextMenuRequested(e -> contextMenu.show(this, e.getScreenX(), e.getScreenY()));

        this.setSpacing(10);
        this.setPadding(new Insets(10, 12, 10, 12));
        this.setStyle(baseStyle);
        this.getChildren().addAll(numLabel, contentBox, primaryActionsBox, editButton);
    }

    private void showDeployKeyDialog(Access access, Runnable updateStyle) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Setup SSH Key Autologin");
        dialog.setHeaderText("Deploy Shared SSH Key to " + access.getName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Enter SSH Password");

        VBox content = new VBox(10,
                new Label("Enter your SSH password to upload the shared public key once:"),
                pwField
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        Platform.runLater(pwField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return pwField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(password -> {
            if (password.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Password cannot be empty!");
                alert.showAndWait();
                return;
            }

            hostCommunicator.deploySharedKeyAsync(access, password)
                    .thenRun(() -> Platform.runLater(() -> {
                        updateStyle.run();
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("SSH Key Deployed");
                        successAlert.setContentText("SSH Key has been successfully registered on " + access.getName() + ".\nAutologin is now active!");
                        successAlert.showAndWait();
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Deployment Failed");
                            errorAlert.setHeaderText("Failed to setup SSH Key");
                            errorAlert.setContentText("Error details:\n" + ex.getCause().getMessage());
                            errorAlert.showAndWait();
                        });
                        return null;
                    });
        });
    }

    private void showResourcesDialog(Access access) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Resources for " + access.getName());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        VBox rootContainer = new VBox(12);
        rootContainer.setPadding(new Insets(15));
        rootContainer.setPrefWidth(620);
        rootContainer.setMinWidth(600);

        HBox headerBox = new HBox(10);
        headerBox.setStyle("-fx-alignment: center-left;");
        Label titleLbl = new Label("Associated Resources");
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);

        Button addResBtn = new Button("+ Add Resource");
        addResBtn.getStyleClass().add("accent");
        addResBtn.setMinWidth(Region.USE_PREF_SIZE);
        addResBtn.setCursor(javafx.scene.Cursor.HAND);

        headerBox.getChildren().addAll(titleLbl, addResBtn);
        rootContainer.getChildren().add(headerBox);

        VBox listContainer = new VBox(10);
        listContainer.setPadding(new Insets(2));

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(320);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rootContainer.getChildren().add(scrollPane);

        Runnable refreshResourcesList = new Runnable() {
            @Override
            public void run() {
                listContainer.getChildren().clear();
                List<Resource> resources = ResourceDAO.getResourcesByAccessId(access.getId());
                if (resources.isEmpty()) {
                    Label noResLabel = new Label("No resources found for this access.");
                    noResLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-padding: 10;");
                    listContainer.getChildren().add(noResLabel);
                } else {
                    for (Resource resource : resources) {
                        HBox row = new HBox(10);
                        final String defaultRowStyle = "-fx-padding: 10 12 10 12; -fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-default; -fx-alignment: center-left;";
                        final String hoverRowStyle = "-fx-padding: 10 12 10 12; -fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-subtle; -fx-alignment: center-left;";
                        row.setStyle(defaultRowStyle);

                        Label nameLbl = new Label(resource.getName());
                        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                        Label urlLbl = new Label(resource.getUrl());
                        urlLbl.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 11px;");
                        urlLbl.setTooltip(new Tooltip(resource.getUrl()));

                        VBox details = new VBox(2, nameLbl, urlLbl);
                        details.setMinWidth(220);
                        HBox.setHgrow(details, Priority.ALWAYS);

                        // Primary action (Open) always visible
                        Button openBtn = new Button("Open");
                        openBtn.getStyleClass().add("accent");
                        openBtn.setMinWidth(Region.USE_PREF_SIZE);
                        openBtn.setCursor(javafx.scene.Cursor.HAND);
                        openBtn.setOnAction(e -> hostCommunicator.openUrl(resource.getUrl()));

                        // Secondary actions (Edit, Delete) semi-transparent by default, full on hover
                        Button editResBtn = new Button("Edit");
                        editResBtn.getStyleClass().add("flat");
                        editResBtn.setMinWidth(Region.USE_PREF_SIZE);
                        editResBtn.setCursor(javafx.scene.Cursor.HAND);
                        editResBtn.setOnAction(e -> {
                            if (mainApp != null) {
                                mainApp.showEditResourcePage(resource);
                                run();
                            }
                        });

                        Button deleteResBtn = new Button("Delete");
                        deleteResBtn.getStyleClass().add("flat");
                        deleteResBtn.setStyle("-fx-text-fill: -color-danger-fg;");
                        deleteResBtn.setMinWidth(Region.USE_PREF_SIZE);
                        deleteResBtn.setCursor(javafx.scene.Cursor.HAND);
                        deleteResBtn.setOnAction(e -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Delete Resource");
                            alert.setHeaderText("Delete " + resource.getName() + "?");
                            alert.setContentText("Are you sure you want to delete this resource?");
                            alert.showAndWait().ifPresent(btnType -> {
                                if (btnType == ButtonType.OK) {
                                    ResourceDAO.deleteResource(resource.getId());
                                    run();
                                }
                            });
                        });

                        HBox secondaryBox = new HBox(4, editResBtn, deleteResBtn);
                        secondaryBox.setStyle("-fx-alignment: center-right;");
                        secondaryBox.setOpacity(0.35);

                        row.setOnMouseEntered(e -> {
                            row.setStyle(hoverRowStyle);
                            secondaryBox.setOpacity(1.0);
                        });
                        row.setOnMouseExited(e -> {
                            row.setStyle(defaultRowStyle);
                            secondaryBox.setOpacity(0.35);
                        });

                        row.getChildren().addAll(details, openBtn, secondaryBox);
                        listContainer.getChildren().add(row);
                    }
                }
            }
        };

        addResBtn.setOnAction(e -> {
            if (mainApp != null) {
                mainApp.showAddResourcePage(access);
                refreshResourcesList.run();
            }
        });

        refreshResourcesList.run();

        dialog.getDialogPane().setContent(rootContainer);
        dialog.showAndWait();
    }
}
