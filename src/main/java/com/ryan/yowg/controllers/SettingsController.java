package com.ryan.yowg.controllers;

import com.ryan.yowg.MainApp;
import com.ryan.yowg.dao.AccessDAO;
import com.ryan.yowg.dao.CredentialDAO;
import com.ryan.yowg.dao.DatabaseConnector;
import com.ryan.yowg.dao.ResourceDAO;
import com.ryan.yowg.dao.SettingsDAO;
import com.ryan.yowg.dao.WireguardDAO;
import com.ryan.yowg.models.Credential;
import com.ryan.yowg.models.Wireguard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class SettingsController implements Initializable {
    private final MainApp mainApp;

    @FXML
    private Button btnHome;
    @FXML
    private PasswordField sudoPasswordField;
    @FXML
    private Button btnSaveSudoPassword;
    @FXML
    private Label lblSudoStatus;
    @FXML
    private VBox settingsEntriesContainer;

    @FXML
    private CheckBox chkCredentials;
    @FXML
    private CheckBox chkAccessResources;
    @FXML
    private CheckBox chkWireguards;
    @FXML
    private CheckBox chkSystemConfigs;
    @FXML
    private CheckBox chkAppSettings;

    @FXML
    private Button btnCleanSelected;
    @FXML
    private Button btnFactoryReset;

    public SettingsController(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (btnHome != null) {
            btnHome.setOnAction(e -> {
                if (mainApp != null) {
                    mainApp.showMainPage();
                }
            });
        }

        loadSudoPasswordSetting();
        loadAllSettingsEntries();

        if (btnSaveSudoPassword != null) {
            btnSaveSudoPassword.setOnAction(this::handleSaveSudoPassword);
        }

        if (btnCleanSelected != null) {
            btnCleanSelected.setOnAction(this::handleCleanSelected);
        }

        if (btnFactoryReset != null) {
            btnFactoryReset.setOnAction(this::handleFactoryReset);
        }
    }

    private void loadSudoPasswordSetting() {
        String sudoPassword = SettingsDAO.getSetting("sudo_password");
        if (sudoPassword != null) {
            sudoPasswordField.setText(sudoPassword);
        }
    }

    private void handleSaveSudoPassword(ActionEvent event) {
        String newPassword = sudoPasswordField.getText();
        SettingsDAO.saveSetting("sudo_password", newPassword);
        lblSudoStatus.setText("Sudo password updated successfully!");
        loadAllSettingsEntries();
    }

    private void loadAllSettingsEntries() {
        settingsEntriesContainer.getChildren().clear();

        Map<String, String> settingsMap = getAllSettingsFromDB();
        if (settingsMap.isEmpty()) {
            Label emptyLabel = new Label("No settings found in settings table.");
            emptyLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-padding: 5;");
            settingsEntriesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            Label keyLabel = new Label(key);
            keyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            keyLabel.setMinWidth(180);

            TextField valField = new TextField(value);
            valField.setStyle("-fx-font-size: 12px;");
            HBox.setHgrow(valField, Priority.ALWAYS);

            Button btnSaveRow = new Button("Save");
            btnSaveRow.getStyleClass().add("flat");
            btnSaveRow.setCursor(javafx.scene.Cursor.HAND);
            btnSaveRow.setOnAction(e -> {
                SettingsDAO.saveSetting(key, valField.getText());
                lblSudoStatus.setText("Updated setting key: '" + key + "'");
            });

            Button btnDeleteRow = new Button("Delete");
            btnDeleteRow.getStyleClass().add("flat");
            btnDeleteRow.setStyle("-fx-text-fill: -color-danger-fg;");
            btnDeleteRow.setCursor(javafx.scene.Cursor.HAND);
            btnDeleteRow.setOnAction(e -> {
                deleteSettingKey(key);
                loadAllSettingsEntries();
                loadSudoPasswordSetting();
            });

            HBox row = new HBox(10, keyLabel, valField, btnSaveRow, btnDeleteRow);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 6 10; -fx-border-color: -color-border-default; -fx-border-radius: 4; -fx-background-radius: 4; -fx-background-color: -color-bg-subtle;");

            settingsEntriesContainer.getChildren().add(row);
        }
    }

    private Map<String, String> getAllSettingsFromDB() {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT key, value FROM settings ORDER BY key ASC";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("key"), rs.getString("value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void deleteSettingKey(String key) {
        String sql = "DELETE FROM settings WHERE key = '" + key.replace("'", "''") + "'";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCleanSelected(ActionEvent event) {
        boolean cleanCreds = chkCredentials.isSelected();
        boolean cleanAccessRes = chkAccessResources.isSelected();
        boolean cleanWg = chkWireguards.isSelected();
        boolean cleanSysConfigs = chkSystemConfigs.isSelected();
        boolean cleanSettings = chkAppSettings.isSelected();

        if (!cleanCreds && !cleanAccessRes && !cleanWg && !cleanSysConfigs && !cleanSettings) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Pembersihan Data");
            alert.setHeaderText("Tidak ada opsi dipilih");
            alert.setContentText("Silakan pilih setidaknya satu kategori data yang ingin dibersihkan.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Pembersihan Data");
        confirmAlert.setHeaderText("Konfirmasi Hapus Data Terpilih");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus data yang dipilih? Tindakan ini tidak dapat dibatalkan.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            performCleanup(cleanCreds, cleanAccessRes, cleanWg, cleanSysConfigs, cleanSettings);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Sukses");
            successAlert.setHeaderText("Pembersihan Data Selesai");
            successAlert.setContentText("Data terpilih telah berhasil dibersihkan dari aplikasi.");
            successAlert.showAndWait();

            loadSudoPasswordSetting();
            loadAllSettingsEntries();
        }
    }

    private void handleFactoryReset(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Factory Reset");
        alert.setHeaderText("PERINGATAN FACTORY RESET!");
        alert.setContentText("Tindakan ini akan MENGHAPUS SELURUH DATA aplikasi (WireGuard Tunnels, Access Nodes, Resources, Credentials, SSH Keys, dan Settings).\n\nApakah Anda yakin ingin melanjutkan?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            performCleanup(true, true, true, false, true);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Factory Reset Completed");
            successAlert.setHeaderText("Aplikasi Telah Di-reset");
            successAlert.setContentText("Seluruh data aplikasi dan kredensial berhasil dibersihkan.");
            successAlert.showAndWait();

            loadSudoPasswordSetting();
            loadAllSettingsEntries();
        }
    }

    public static void performCleanup(boolean cleanCreds, boolean cleanAccessRes, boolean cleanWg, boolean cleanSysConfigs, boolean cleanSettings) {
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {

            if (cleanAccessRes) {
                stmt.executeUpdate("DELETE FROM resources;");
                stmt.executeUpdate("DELETE FROM access;");
                System.out.println("[Cleanup] Access nodes & resources cleared.");
            }

            if (cleanCreds) {
                stmt.executeUpdate("DELETE FROM credentials;");
                deleteLocalSshKeys();
                System.out.println("[Cleanup] Credentials & SSH keys cleared.");
            }

            if (cleanWg) {
                stmt.executeUpdate("DELETE FROM wireguards;");
                System.out.println("[Cleanup] Wireguard tunnels database table cleared.");
            }

            if (cleanSettings) {
                stmt.executeUpdate("DELETE FROM settings;");
                System.out.println("[Cleanup] Settings table cleared.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteLocalSshKeys() {
        try {
            String home = System.getProperty("user.home");
            File sshDir = new File(home + "/.ssh/yo-wg");
            if (sshDir.exists() && sshDir.isDirectory()) {
                File[] files = sshDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        f.delete();
                    }
                }
                sshDir.delete();
            }
        } catch (Exception e) {
            System.err.println("[Cleanup] Error deleting local SSH keys: " + e.getMessage());
        }
    }
}
