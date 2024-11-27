package com.ryan.yowg.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AccessComp extends HBox {
    public AccessComp(int num, String name, String address){
        Label numLabel = new Label(String.valueOf(num));
        Label nameLabel = new Label(name);
        TextField addressField = new TextField(address);
        addressField.setEditable(false);
        VBox contentBox = new VBox(nameLabel, addressField);
        contentBox.setSpacing(5);

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(numLabel, contentBox);
    }
}
