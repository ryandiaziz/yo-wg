package com.ryan.yowg.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.ryan.yowg.utils.Execute;

import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Resource;
import com.ryan.yowg.dao.ResourceDAO;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import java.util.List;

public class AccessComp extends HBox {
    public AccessComp(int num, Access access) {
        Label numLabel = new Label(String.valueOf(num));
        Label nameLabel = new Label(access.getName());
        TextField addressField = new TextField(access.getAddress());
        addressField.setEditable(false);
        VBox contentBox = new VBox(nameLabel, addressField);
        contentBox.setSpacing(5);

        Button sshButton = new Button("SSH");
        sshButton.setOnAction(
                e -> Execute.openSSHTerminal(access.getAddress(), access.getSshUser(), access.getSshPort()));

        Button resourcesButton = new Button("Resources");
        resourcesButton.setOnAction(e -> showResourcesDialog(access));

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(numLabel, contentBox, sshButton, resourcesButton);
    }

    private void showResourcesDialog(Access access) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Resources for " + access.getName());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setPrefWidth(400);

        List<Resource> resources = ResourceDAO.getResourcesByAccessId(access.getId());
        if (resources.isEmpty()) {
            container.getChildren().add(new Label("No resources found."));
        } else {
            for (Resource resource : resources) {
                HBox row = new HBox(10);
                Label nameLbl = new Label(resource.getName());
                Label urlLbl = new Label(resource.getUrl());
                urlLbl.setStyle("-fx-text-fill: grey;");

                Button openBtn = new Button("Open");
                openBtn.setOnAction(e -> Execute.openUrl(resource.getUrl()));

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
