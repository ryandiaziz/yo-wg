package com.ryan.yowg.controllers;

import com.ryan.yowg.components.AccessComp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Access;
import com.ryan.yowg.models.Wireguard;
import com.ryan.yowg.utils.Execute;
import com.ryan.yowg.utils.ReadConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MainController implements Initializable {
    @FXML
    private VBox listWGContainer;
    @FXML
    private TextArea wgDetailInfo;
    @FXML
    private VBox accessContainer;

    // Tambahkan ToggleGroup sebagai atribut
    public static ToggleGroup toggleGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inisialisasi ToggleGroup
        toggleGroup = new ToggleGroup();

        CompletableFuture.runAsync(() -> {
            List<Wireguard> wireguards = WireguardDAO.getAllWireguards();

            Platform.runLater(() -> {
                // Tambahkan setiap WG ke dalam VBox sebagai RadioButton dan atur ToggleGroup
                for (Wireguard item : wireguards) {
                    RadioButton radioButton = new RadioButton(item.getName());
                    radioButton.setToggleGroup(toggleGroup); // Setiap RadioButton menjadi bagian dari ToggleGroup
                    listWGContainer.getChildren().add(radioButton);
                }

                // Tambahkan event listener untuk mendeteksi perubahan pilihan
                toggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                    if (oldToggle != null){
                        RadioButton oldRadioButton = (RadioButton) oldToggle;
                        Execute.wgAction("down", oldRadioButton.getText());
                    }
                    if (newToggle != null) {
                        RadioButton selectedRadioButton = (RadioButton) newToggle;
                        Execute.wgAction("up", selectedRadioButton.getText());

                        CompletableFuture.runAsync(() -> {
                            Wireguard wireguard = WireguardDAO.findWireguardByName(selectedRadioButton.getText());
                            if (wireguard == null) throw new AssertionError();
                            List<Access> accessList = AccessDAO.getAccessByWireguard(wireguard.getId());

                            Platform.runLater(() -> {
                                wgDetailInfo.setText(wireguard.getContent());
                                setAccessContainer(accessList);
                            });
                        });
                    }
                });
            });
        });
    }

    private void setAccessContainer(List<Access> accessList){
        accessContainer.getChildren().removeAll();

        int num = 1;
        for (Access access : accessList){
            accessContainer.getChildren().add(
                    new AccessComp(num, access.getName(), access.getAddress())
            );
            num++;
        }
    }

    private void setClear(){
        wgDetailInfo.setText("");
        accessContainer.getChildren().removeAll();
    }
}