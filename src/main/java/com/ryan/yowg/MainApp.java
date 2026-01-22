package com.ryan.yowg;

import com.ryan.yowg.controllers.access.AccessController;
import com.ryan.yowg.controllers.resource.ResourceController;
import com.ryan.yowg.controllers.resource.EditResourceController;
import com.ryan.yowg.controllers.RootController;
import com.ryan.yowg.controllers.wireguard.WireguardController;
import com.ryan.yowg.dao.DatabaseSetup;
import com.ryan.yowg.models.Resource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) {
        // Setup database
        DatabaseSetup.createTable();

        this.primaryStage = stage;
        this.primaryStage.setResizable(false);
        this.showRootPage();
        this.primaryStage.show();
    }

    public void showRootPage() {
        try {
            RootController rootController = new RootController(this);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/root-view.fxml"));
            loader.setControllerFactory(param -> rootController);
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
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
            VBox mainPage = loader.load();
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
            WireguardController wireguardController = new WireguardController(this);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/wireguards-view.fxml"));
            loader.setControllerFactory(param -> wireguardController);
            AnchorPane mainPage = loader.load();
            rootLayout.setCenter(mainPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAddWgPage() {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/add-wg-view.fxml"));
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

    public void showAddAccessPage() {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/add-access-view.fxml"));
            Parent parent = loader.load();

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

    public void showAddResourcePage() {
        try {
            // Load FXML dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/add-resource-view.fxml"));
            Parent parent = loader.load();

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

    public static void main(String[] args) {
        launch();
    }
}