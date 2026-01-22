package com.ryan.yowg.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.ryan.yowg.utils.Execute;

import com.ryan.yowg.models.Access;

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

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(numLabel, contentBox, sshButton);
    }
}
