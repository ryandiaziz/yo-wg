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
import javafx.scene.shape.SVGPath;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import java.util.List;

public class AccessComp extends HBox {
    public AccessComp(int num, Access access) {
        Label numLabel = new Label(String.valueOf(num));
        numLabel.setMinWidth(20);

        Label nameLabel = new Label(access.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");
        TextField addressField = new TextField(access.getAddress());
        addressField.setEditable(false);
        addressField.setStyle("-fx-background-color: transparent; -fx-padding: 0;"); // Make it look like label but
                                                                                     // selectable

        VBox contentBox = new VBox(nameLabel, addressField);
        contentBox.setSpacing(2);
        HBox.setHgrow(contentBox, Priority.ALWAYS); // Give it remaining space

        // SSH Icon (Terminal)
        SVGPath sshIcon = new SVGPath();
        sshIcon.setContent(
                "M20,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V6C22,4.89 21.1,4 20,4M20,18H4V6H20V18M7.5,15L9,16.5L13.5,12L9,7.5L7.5,9L10.5,12L7.5,15Z");
        sshIcon.setScaleX(0.7);
        sshIcon.setScaleY(0.7);
        Button sshButton = new Button();
        sshButton.setGraphic(sshIcon);
        sshButton.setTooltip(new Tooltip("SSH"));
        sshButton.setOnAction(
                e -> Execute.openSSHTerminal(access.getAddress(), access.getSshUser(), access.getSshPort()));

        // Ping Terminal Icon (Pulse/Activity)
        SVGPath pingIcon = new SVGPath();
        pingIcon.setContent(
                "M3,13H5.79L6.08,13.03L9,20L15,4L17.92,12.97L18.21,13H21V15H17.21L15,19L9,3L6.08,18.97L5.79,19H3V13Z");
        pingIcon.setScaleX(0.7);
        pingIcon.setScaleY(0.7);
        Button pingButton = new Button();
        pingButton.setGraphic(pingIcon);
        pingButton.setTooltip(new Tooltip("Ping Terminal"));
        pingButton.setOnAction(e -> Execute.openPingTerminal(access.getAddress()));

        // Resources Icon (List/Folder)
        SVGPath resourcesIcon = new SVGPath();
        resourcesIcon
                .setContent("M4 14h4v-4H4v4zm0 5h4v-4H4v4zM4 9h4V5H4v4zm5 5h12v-4H9v4zm0 5h12v-4H9v4zM9 5v4h12V5H9z");
        resourcesIcon.setScaleX(0.7);
        resourcesIcon.setScaleY(0.7);
        Button resourcesButton = new Button();
        resourcesButton.setGraphic(resourcesIcon);
        resourcesButton.setTooltip(new Tooltip("Resources"));
        resourcesButton.setOnAction(e -> showResourcesDialog(access));

        this.setSpacing(10);
        this.setPadding(new Insets(5));
        this.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        this.getChildren().addAll(numLabel, contentBox, sshButton, pingButton, resourcesButton);
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
