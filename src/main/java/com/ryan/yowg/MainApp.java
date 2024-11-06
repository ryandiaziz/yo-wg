package com.ryan.yowg;

import com.ryan.yowg.controllers.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage stage){
        this.primaryStage = stage;
        this.primaryStage.setResizable(false);
        showRootPage();
        this.primaryStage.show();
    }

    public void showRootPage() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("views/root-view.fxml"));
            rootLayout = loader.load();

            RootController rootController = loader.getController();

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

    public static void main(String[] args) {
        launch();
    }
}