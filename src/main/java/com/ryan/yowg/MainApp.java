package com.ryan.yowg;

import com.ryan.yowg.controllers.access.AddAccessController;
import com.ryan.yowg.controllers.access.EditAccessController;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.controllers.access.AccessController;
import com.ryan.yowg.controllers.resource.AddResourceController;
import com.ryan.yowg.controllers.resource.ResourceController;
import com.ryan.yowg.controllers.resource.EditResourceController;
import com.ryan.yowg.controllers.RootController;
import com.ryan.yowg.controllers.wireguard.WireguardController;
import com.ryan.yowg.controllers.wireguard.EditWgController;
import com.ryan.yowg.controllers.wireguard.AddWgController;
import com.ryan.yowg.controllers.MainController;
import com.ryan.yowg.services.TunnelManager;
import com.ryan.yowg.services.SystemTunnelManager;
import com.ryan.yowg.services.HostCommunicator;
import com.ryan.yowg.services.SystemHostCommunicator;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.dao.DatabaseSetup;
import com.ryan.yowg.models.Resource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.VBox;
import java.util.Optional;
import com.ryan.yowg.dao.SettingsDAO;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    
    private final TunnelManager tunnelManager = new SystemTunnelManager();
    private final HostCommunicator hostCommunicator = new SystemHostCommunicator();
    private final com.ryan.yowg.dao.Repository repository = new com.ryan.yowg.dao.SqliteRepository();

    private boolean isDarkMode = true;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void toggleTheme() {
        if (isDarkMode) {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            isDarkMode = false;
        } else {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            isDarkMode = true;
        }
    }

    @Override
    public void start(Stage stage) {
        // Setup database storage module
        repository.initialize();

        // Prompt for sudo password if not set
        String sudoPassword = repository.getSetting("sudo_password");
        if (sudoPassword == null || sudoPassword.trim().isEmpty()) {
            promptSudoPassword();
        }

        // Apply default modern theme
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        this.primaryStage = stage;
        this.primaryStage.setTitle("Yo-WG Tunnel Manager");
        this.primaryStage.setMinWidth(800);
        this.primaryStage.setMinHeight(600);
        this.primaryStage.setResizable(true);
        this.showRootPage();
        this.primaryStage.show();
    }

    public void showRootPage() {
        try {
            RootController rootController = new RootController(this, tunnelManager);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/root-view.fxml"));
            loader.setControllerFactory(param -> rootController);
            rootLayout = loader.load();

            // Show the scene containing the root layout with default width and height
            Scene scene = new Scene(rootLayout, 1000, 700);
            primaryStage.setScene(scene);
            showMainPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/main-view.fxml"));
            loader.setControllerFactory(param -> new MainController(this, tunnelManager, hostCommunicator));
            Parent mainPage = loader.load();
            rootLayout.setCenter(mainPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAccessMenuPage() {
        try {
            AccessController accessController = new AccessController(this);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/access-view.fxml"));
            loader.setControllerFactory(param -> accessController);
            AnchorPane mainPage = loader.load();
            rootLayout.setCenter(mainPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showResourceMenuPage() {
        try {
            ResourceController resourceController = new ResourceController(this);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/resource-view.fxml"));
            loader.setControllerFactory(param -> resourceController);
            AnchorPane mainPage = loader.load();
            rootLayout.setCenter(mainPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showWireguardMenuPage() {
        try {
            WireguardController wireguardController = new WireguardController(this, tunnelManager);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/wireguards-view.fxml"));
            loader.setControllerFactory(param -> wireguardController);
            AnchorPane mainPage = loader.load();
            rootLayout.setCenter(mainPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCredentialMenuPage() {
        try {
            com.ryan.yowg.controllers.CredentialController credentialController = new com.ryan.yowg.controllers.CredentialController(this);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/credentials-view.fxml"));
            loader.setControllerFactory(param -> credentialController);
            AnchorPane mainPage = loader.load();
            rootLayout.setCenter(mainPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAddCredentialPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/add-credential-view.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add Credential");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEditCredentialPage(com.ryan.yowg.models.Credential credential) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/edit-credential-view.fxml"));
            Parent parent = loader.load();

            com.ryan.yowg.controllers.EditCredentialController controller = loader.getController();
            controller.setCredential(credential);

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Credential");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGenerateKeyPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/generate-key-view.fxml"));
            loader.setControllerFactory(param -> new com.ryan.yowg.controllers.GenerateKeyController(hostCommunicator));
            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Generate & Deploy SSH Key");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(parent));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAddWgPage() {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/add-wg-view.fxml"));
            loader.setControllerFactory(param -> new AddWgController(tunnelManager));
            Parent parent = loader.load();

            // Buat Stage baru untuk dialog
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add New");
            stage.initModality(Modality.WINDOW_MODAL); // Set sebagai modal dialog
            stage.setScene(new Scene(parent));
            stage.showAndWait(); // Tunggu sampai dialog ditutup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEditWgPage(Wireguard wireguard) {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/edit-wg-view.fxml"));
            loader.setControllerFactory(param -> new EditWgController(tunnelManager));
            Parent parent = loader.load();

            EditWgController controller = loader.getController();
            controller.setWireguard(wireguard);

            // Buat Stage baru untuk dialog
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Wireguard");
            stage.initModality(Modality.WINDOW_MODAL); // Set sebagai modal dialog
            stage.setScene(new Scene(parent));
            stage.showAndWait(); // Tunggu sampai dialog ditutup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAddAccessPage() {
        showAddAccessPage(null);
    }

    public void showAddAccessPage(Wireguard preselectedWg) {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/add-access-view.fxml"));
            Parent parent = loader.load();

            if (preselectedWg != null) {
                AddAccessController controller = loader.getController();
                controller.setPreselectedWireguard(preselectedWg);
            }

            // Buat Stage baru untuk dialog
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add Access");
            stage.initModality(Modality.WINDOW_MODAL); // Set sebagai modal dialog
            stage.setScene(new Scene(parent));
            stage.showAndWait(); // Tunggu sampai dialog ditutup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEditAccessPage(Access access) {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/edit-access-view.fxml"));
            Parent parent = loader.load();

            EditAccessController controller = loader.getController();
            controller.setAccess(access);

            // Buat Stage baru untuk dialog
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Access");
            stage.initModality(Modality.WINDOW_MODAL); // Set sebagai modal dialog
            stage.setScene(new Scene(parent));
            stage.showAndWait(); // Tunggu sampai dialog ditutup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAddResourcePage() {
        showAddResourcePage(null);
    }

    public void showAddResourcePage(Access preselectedAccess) {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/add-resource-view.fxml"));
            Parent parent = loader.load();

            if (preselectedAccess != null) {
                AddResourceController controller = loader.getController();
                controller.setPreselectedAccess(preselectedAccess);
            }

            // Buat Stage baru untuk dialog
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add Resource");
            stage.initModality(Modality.WINDOW_MODAL); // Set sebagai modal dialog
            stage.setScene(new Scene(parent));
            stage.showAndWait(); // Tunggu sampai dialog ditutup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEditResourcePage(Resource resource) {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/edit-resource-view.fxml"));
            Parent parent = loader.load();

            EditResourceController controller = loader.getController();
            controller.setResource(resource);

            // Buat Stage baru untuk dialog
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Edit Resource");
            stage.initModality(Modality.WINDOW_MODAL); // Set sebagai modal dialog
            stage.setScene(new Scene(parent));
            stage.showAndWait(); // Tunggu sampai dialog ditutup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void promptSudoPassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Initial Setup");
        dialog.setHeaderText("Welcome to Yo-WG!\nPlease enter your system (sudo) password to allow WireGuard management.");
        
        PasswordField pwd = new PasswordField();
        pwd.setPromptText("Sudo Password");
        
        VBox vbox = new VBox(10);
        vbox.getChildren().add(new Label("Sudo Password:"));
        vbox.getChildren().add(pwd);
        dialog.getDialogPane().setContent(vbox);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return pwd.getText();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            SettingsDAO.saveSetting("sudo_password", password);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}