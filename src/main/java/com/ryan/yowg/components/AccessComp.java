package com.ryan.yowg.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.ryan.yowg.services.HostCommunicator;

import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Resource;
import com.ryan.yowg.dao.ResourceDAO;
import javafx.scene.shape.SVGPath;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Priority;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class AccessComp extends HBox {
    private final HostCommunicator hostCommunicator;

    public AccessComp(int num, Access access, HostCommunicator hostCommunicator) {
        this.hostCommunicator = hostCommunicator;
        
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

        // SSH Icon (Terminal)
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
                com.ryan.yowg.models.Credential credential = com.ryan.yowg.dao.CredentialDAO.getCredentialById(access.getCredentialId());
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

        // Ping Terminal Icon (Pulse/Activity)
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

        // Resources Icon (List/Folder)
        SVGPath resourcesIcon = new SVGPath();
        resourcesIcon
                .setContent("M4 14h4v-4H4v4zm0 5h4v-4H4v4zM4 9h4V5H4v4zm5 5h12v-4H9v4zm0 5h12v-4H9v4zM9 5v4h12V5H9z");
        resourcesIcon.setScaleX(0.7);
        resourcesIcon.setScaleY(0.7);
        resourcesIcon.setStyle("-fx-fill: -color-fg-default;");
        Button resourcesButton = new Button();
        resourcesButton.setGraphic(resourcesIcon);
        resourcesButton.getStyleClass().add("flat");
        resourcesButton.setCursor(javafx.scene.Cursor.HAND);
        resourcesButton.setTooltip(new Tooltip("Resources"));
        resourcesButton.setOnAction(e -> showResourcesDialog(access));

        // Key/Autologin Icon (Key)
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
                // Key is active, show options to disable/redeploy
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("SSH Key Autologin");
                alert.setHeaderText("SSH Key autologin is active for " + access.getName());
                alert.setContentText("Choose an action below:");

                javafx.scene.control.ButtonType btnRemove = new javafx.scene.control.ButtonType("Disable Autologin");
                javafx.scene.control.ButtonType btnRedeploy = new javafx.scene.control.ButtonType("Redeploy Key");
                javafx.scene.control.ButtonType btnCancel = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(btnRemove, btnRedeploy, btnCancel);

                java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnRemove) {
                    access.setCredentialId(null);
                    com.ryan.yowg.dao.AccessDAO.updateAccess(access);
                    updateKeyButtonStyle.run();
                } else if (result.isPresent() && result.get() == btnRedeploy) {
                    showDeployKeyDialog(access, updateKeyButtonStyle);
                }
            } else {
                // Key not active, show deploy dialog
                showDeployKeyDialog(access, updateKeyButtonStyle);
            }
        });

        this.setSpacing(10);
        this.setPadding(new Insets(10, 12, 10, 12));
        this.setStyle("-fx-border-color: -color-border-default; -fx-border-radius: 6; -fx-background-radius: 6; -fx-background-color: -color-bg-default; -fx-alignment: center-left;");
        this.getChildren().addAll(numLabel, contentBox, sshButton, pingButton, resourcesButton, keyButton);
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

        // Request focus on the password field by default
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

            // Run in background thread
            CompletableFuture.runAsync(() -> {
                try {
                    // 1. Check sshpass
                    Process whichProc = Runtime.getRuntime().exec(new String[]{"which", "sshpass"});
                    if (whichProc.waitFor() != 0) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("sshpass Missing");
                            alert.setHeaderText("sshpass is required");
                            alert.setContentText("The command 'sshpass' is required to deploy the key automatically.\n\nPlease install it using:\nsudo apt install sshpass");
                            alert.showAndWait();
                        });
                        return;
                    }

                    // 2. Resolve/Create Shared Key Profile
                    String home = System.getProperty("user.home");
                    File sshDir = new File(home + "/.ssh/yo-wg");
                    if (!sshDir.exists()) {
                        sshDir.mkdirs();
                    }

                    String privateKeyPath = sshDir.getAbsolutePath() + "/id_yowg_shared";
                    File privFile = new File(privateKeyPath);
                    File pubFile = new File(privateKeyPath + ".pub");

                    // Check if credential exists in DB
                    com.ryan.yowg.models.Credential sharedCred = null;
                    java.util.List<com.ryan.yowg.models.Credential> allCreds = com.ryan.yowg.dao.CredentialDAO.getAllCredentials();
                    for (com.ryan.yowg.models.Credential c : allCreds) {
                        if (c.getName().equals("Shared Yo-WG Key")) {
                            sharedCred = c;
                            break;
                        }
                    }

                    // If files don't exist or credential is not in DB, generate it
                    if (!privFile.exists() || !pubFile.exists() || sharedCred == null) {
                        if (privFile.exists()) privFile.delete();
                        if (pubFile.exists()) pubFile.delete();

                        String[] keygenCmd = {
                            "ssh-keygen", "-t", "ed25519",
                            "-f", privateKeyPath,
                            "-N", "", // No passphrase
                            "-q"
                        };
                        Process keygenProc = Runtime.getRuntime().exec(keygenCmd);
                        if (keygenProc.waitFor() != 0) {
                            throw new Exception("Local keypair generation using ssh-keygen failed.");
                        }

                        if (sharedCred == null) {
                            sharedCred = new com.ryan.yowg.models.Credential("Shared Yo-WG Key", "", "key", privateKeyPath);
                            com.ryan.yowg.dao.CredentialDAO.insertCredential(sharedCred);
                            
                            // Re-fetch to get database ID
                            allCreds = com.ryan.yowg.dao.CredentialDAO.getAllCredentials();
                            for (com.ryan.yowg.models.Credential c : allCreds) {
                                if (c.getName().equals("Shared Yo-WG Key")) {
                                    sharedCred = c;
                                    break;
                                }
                            }
                        }
                    }

                    // 3. Read public key
                    String pubKeyContent = new String(Files.readAllBytes(pubFile.toPath())).trim();

                    // 4. Deploy public key to server using sshpass
                    String remoteSetupCmd = "mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo '" + pubKeyContent + "' >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys";
                    String[] sshCmd = {
                        "sshpass", "-p", password,
                        "ssh", "-p", String.valueOf(access.getSshPort()),
                        "-o", "StrictHostKeyChecking=no",
                        "-o", "ConnectTimeout=10",
                        access.getSshUser() + "@" + access.getAddress(),
                        remoteSetupCmd
                    };

                    Process sshProc = Runtime.getRuntime().exec(sshCmd);

                    // Capture error stream
                    BufferedReader reader = new BufferedReader(new InputStreamReader(sshProc.getErrorStream()));
                    StringBuilder errOutput = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errOutput.append(line).append("\n");
                    }

                    int exitCode = sshProc.waitFor();
                    if (exitCode == 0) {
                        // Success: link access node to shared credential ID
                        final int credId = sharedCred.getId();
                        access.setCredentialId(credId);
                        com.ryan.yowg.dao.AccessDAO.updateAccess(access);

                        Platform.runLater(() -> {
                            updateStyle.run();
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText("SSH Key Deployed");
                            successAlert.setContentText("SSH Key has been successfully registered on " + access.getName() + ".\nAutologin is now active!");
                            successAlert.showAndWait();
                        });
                    } else {
                        String errMsg = errOutput.toString().trim();
                        if (errMsg.isEmpty()) {
                            errMsg = "Unable to connect or authenticate. Please check password and server connectivity.";
                        }
                        throw new Exception(errMsg);
                    }

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Deployment Failed");
                        errorAlert.setHeaderText("Failed to setup SSH Key");
                        errorAlert.setContentText("Error details:\n" + e.getMessage());
                        errorAlert.showAndWait();
                    });
                }
            });
        });
    }

    private void showResourcesDialog(Access access) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Resources for " + access.getName());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setPrefWidth(400);

        List<Resource> resources = ResourceDAO.getResourcesByAccessId(access.getId());
        if (resources.isEmpty()) {
            Label noResLabel = new Label("No resources found.");
            noResLabel.setStyle("-fx-text-fill: -color-fg-muted;");
            container.getChildren().add(noResLabel);
        } else {
            for (Resource resource : resources) {
                HBox row = new HBox(10);
                row.setStyle("-fx-padding: 8; -fx-border-color: -color-border-default; -fx-border-radius: 4; -fx-background-color: -color-bg-subtle; -fx-alignment: center-left;");
                
                Label nameLbl = new Label(resource.getName());
                nameLbl.setStyle("-fx-font-weight: bold;");
                Label urlLbl = new Label(resource.getUrl());
                urlLbl.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 11px;");

                Button openBtn = new Button("Open");
                openBtn.getStyleClass().add("accent");
                openBtn.setCursor(javafx.scene.Cursor.HAND);
                openBtn.setOnAction(e -> hostCommunicator.openUrl(resource.getUrl()));

                VBox details = new VBox(nameLbl, urlLbl);
                HBox.setHgrow(details, Priority.ALWAYS);

                row.getChildren().addAll(details, openBtn);
                container.getChildren().add(row);
            }
        }

        dialog.getDialogPane().setContent(container);
        dialog.showAndWait();
    }
}
