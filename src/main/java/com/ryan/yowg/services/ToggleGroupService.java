package com.ryan.yowg.services;

import com.ryan.yowg.models.Wireguard;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.List;

public class ToggleGroupService {
    private static final ToggleGroup toggleGroup = new ToggleGroup();

    public static ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    public static void initializeWireguardToggleGroup(List<Wireguard> wireguards) {
        wireguards.forEach(wireguard -> {
            RadioButton radioButton = createRadioButton(wireguard);
            toggleGroup.getToggles().add(radioButton);
        });
    }

    private static RadioButton createRadioButton(Wireguard wireguard) {
        if (wireguard == null || wireguard.getName() == null) {
            throw new IllegalArgumentException("Wireguard data is invalid");
        }
        RadioButton radioButton = new RadioButton(wireguard.getName());
        radioButton.setPadding(new Insets(2));
        radioButton.setToggleGroup(toggleGroup);
        return radioButton;
    }
}