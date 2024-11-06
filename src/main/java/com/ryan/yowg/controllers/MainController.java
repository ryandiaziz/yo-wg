package com.ryan.yowg.controllers;

import com.ryan.yowg.utils.Execute;
import com.ryan.yowg.utils.ReadConfig;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private VBox listWGContainer;
    @FXML
    private Button offWg;
    @FXML
    private void shutdownWg(){
        if (toggleGroup.getSelectedToggle() != null){
            RadioButton selectedToggle = (RadioButton) toggleGroup.getSelectedToggle();
            Execute.command("down", selectedToggle.getText());
            toggleGroup.getSelectedToggle().setSelected(false);
        }
    }

    // Tambahkan ToggleGroup sebagai atribut
    private ToggleGroup toggleGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inisialisasi ToggleGroup
        toggleGroup = new ToggleGroup();

        // Dapatkan daftar WG dari konfigurasi
        List<String> listwg = ReadConfig.getListWg();

        // Tambahkan setiap WG ke dalam VBox sebagai RadioButton dan atur ToggleGroup
        for (String i : listwg) {
            RadioButton radioButton = new RadioButton(i);
            radioButton.setToggleGroup(toggleGroup); // Setiap RadioButton menjadi bagian dari ToggleGroup
            listWGContainer.getChildren().add(radioButton);
        }

        // Tambahkan event listener untuk mendeteksi perubahan pilihan
        toggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (oldToggle != null){
                RadioButton oldRadioButton = (RadioButton) oldToggle;
                Execute.command("down", oldRadioButton.getText());
            }
            if (newToggle != null) {
                RadioButton selectedRadioButton = (RadioButton) newToggle;
                Execute.command("up", selectedRadioButton.getText());
            }
        });
    }
}