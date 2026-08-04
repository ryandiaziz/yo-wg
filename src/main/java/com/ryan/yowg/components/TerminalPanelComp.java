package com.ryan.yowg.components;

import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Built-in terminal panel component with tab support, fullscreen toggle,
 * and show/hide visibility. Sits at the bottom of the main content area
 * inside a SplitPane.
 */
public class TerminalPanelComp extends VBox {

    private final TabPane tabPane;
    private final TerminalConfig darkConfig;
    private final TerminalConfig lightConfig;
    private final AtomicInteger localTerminalCounter = new AtomicInteger(0);
    private Button fullscreenButton;
    private SVGPath fullscreenIcon;

    private boolean isFullscreen = false;
    private boolean isDarkMode = true;

    /** Callback set by RootController to handle fullscreen toggle at layout level. */
    private Runnable onFullscreenToggle;

    /** Callback set by RootController to handle hide at layout level. */
    private Runnable onHideRequest;

    public TerminalPanelComp() {
        // --- Terminal Configs ---
        darkConfig = new TerminalConfig();
        darkConfig.setBackgroundColor(Color.rgb(30, 30, 30));
        darkConfig.setForegroundColor(Color.rgb(204, 204, 204));
        darkConfig.setCursorColor(Color.rgb(204, 204, 204));
        darkConfig.setFontSize(13);
        darkConfig.setScrollbarVisible(true);

        lightConfig = new TerminalConfig();
        lightConfig.setBackgroundColor(Color.rgb(255, 255, 255));
        lightConfig.setForegroundColor(Color.rgb(30, 30, 30));
        lightConfig.setCursorColor(Color.rgb(30, 30, 30));
        lightConfig.setFontSize(13);
        lightConfig.setScrollbarVisible(true);

        // --- Tab Pane ---
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.getStyleClass().add("edge-to-edge");
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // When all tabs are closed, auto-hide the panel
        tabPane.getTabs().addListener((javafx.collections.ListChangeListener<Tab>) change -> {
            if (tabPane.getTabs().isEmpty() && onHideRequest != null) {
                Platform.runLater(onHideRequest::run);
            }
        });

        // --- Toolbar ---
        HBox toolbar = buildToolbar();

        this.getChildren().addAll(toolbar, tabPane);
        this.setStyle("-fx-background-color: -color-bg-default; -fx-border-color: -color-border-default; -fx-border-width: 1 0 0 0;");

        // Initialize fullscreen icon reference
        fullscreenIcon = (SVGPath) fullscreenButton.getGraphic();
    }

    private HBox buildToolbar() {
        HBox toolbar = new HBox(6);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(4, 8, 4, 8));
        toolbar.setStyle("-fx-background-color: -color-bg-subtle; -fx-border-color: -color-border-default; -fx-border-width: 0 0 1 0;");
        toolbar.setMinHeight(32);
        toolbar.setMaxHeight(32);

        // Terminal label
        Label titleLabel = new Label("TERMINAL");
        titleLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: -color-fg-muted;");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // New terminal button (+)
        Button addButton = createToolbarButton(
                "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z",
                "New Terminal"
        );
        addButton.setOnAction(e -> addLocalTerminal());

        // Fullscreen toggle button
        fullscreenButton = createToolbarButton(
                "M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z",
                "Maximize Terminal"
        );
        fullscreenButton.setOnAction(e -> toggleFullscreen());

        // Hide button (chevron down / minimize)
        Button hideButton = createToolbarButton(
                "M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z",
                "Hide Terminal"
        );
        hideButton.setOnAction(e -> {
            if (onHideRequest != null) {
                onHideRequest.run();
            }
        });

        // Close all button (×)
        Button closeAllButton = createToolbarButton(
                "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z",
                "Close All Terminals"
        );
        closeAllButton.setOnAction(e -> closeAllTabs());

        toolbar.getChildren().addAll(titleLabel, spacer, addButton, fullscreenButton, hideButton, closeAllButton);
        return toolbar;
    }

    private Button createToolbarButton(String svgContent, String tooltipText) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgContent);
        icon.setScaleX(0.65);
        icon.setScaleY(0.65);
        icon.setStyle("-fx-fill: -color-fg-muted;");

        Button button = new Button();
        button.setGraphic(icon);
        button.getStyleClass().add("flat");
        button.setCursor(javafx.scene.Cursor.HAND);
        button.setTooltip(new Tooltip(tooltipText));
        button.setPadding(new Insets(2, 4, 2, 4));
        button.setMinSize(24, 24);
        button.setMaxSize(24, 24);

        // Hover effect
        button.setOnMouseEntered(e -> icon.setStyle("-fx-fill: -color-fg-default;"));
        button.setOnMouseExited(e -> icon.setStyle("-fx-fill: -color-fg-muted;"));

        return button;
    }

    // --- Public API ---

    /**
     * Opens a new local bash/shell terminal tab.
     */
    public void addLocalTerminal() {
        int num = localTerminalCounter.incrementAndGet();
        TerminalConfig config = isDarkMode ? darkConfig : lightConfig;
        TerminalBuilder builder = new TerminalBuilder(config);
        TerminalTab tab = builder.newTerminal();
        tab.setText("Terminal " + num);
        tab.setClosable(true);

        setupTabCloseOnProcessExit(tab);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    /**
     * Opens a new terminal tab and runs an SSH command.
     */
    public void addSshTerminal(String address, String user, int port, String credentialType, String credentialSecret) {
        TerminalConfig config = isDarkMode ? darkConfig : lightConfig;
        TerminalBuilder builder = new TerminalBuilder(config);
        TerminalTab tab = builder.newTerminal();
        tab.setText("SSH: " + user + "@" + address);
        tab.setClosable(true);

        // Build SSH command
        String sshCommand;
        if (credentialType != null && credentialType.equals("key") && credentialSecret != null && !credentialSecret.trim().isEmpty()) {
            sshCommand = "ssh -i \"" + credentialSecret + "\" -p " + port + " " + user + "@" + address;
        } else if (credentialType != null && credentialType.equals("password") && credentialSecret != null && !credentialSecret.trim().isEmpty()) {
            // Check if sshpass is installed
            try {
                Process checkProcess = Runtime.getRuntime().exec(new String[]{"which", "sshpass"});
                if (checkProcess.waitFor() == 0) {
                    sshCommand = "sshpass -p \"" + credentialSecret + "\" ssh -p " + port + " " + user + "@" + address;
                } else {
                    sshCommand = "ssh -p " + port + " " + user + "@" + address;
                }
            } catch (Exception e) {
                sshCommand = "ssh -p " + port + " " + user + "@" + address;
            }
        } else {
            sshCommand = "ssh -p " + port + " " + user + "@" + address;
        }

        String finalSshCommand = sshCommand;
        tab.onTerminalFxReady(() -> {
            tab.getTerminal().command(finalSshCommand + "\r");
        });

        setupTabCloseOnProcessExit(tab);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    /**
     * Opens a new terminal tab and runs a ping command.
     */
    public void addPingTerminal(String address) {
        TerminalConfig config = isDarkMode ? darkConfig : lightConfig;
        TerminalBuilder builder = new TerminalBuilder(config);
        TerminalTab tab = builder.newTerminal();
        tab.setText("Ping: " + address);
        tab.setClosable(true);

        tab.onTerminalFxReady(() -> {
            tab.getTerminal().command("ping " + address + "\r");
        });

        setupTabCloseOnProcessExit(tab);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    /**
     * Automatically close the tab when the underlying shell/process exits (e.g. exit command or Ctrl+D).
     */
    private void setupTabCloseOnProcessExit(TerminalTab tab) {
        tab.onTerminalFxReady(() -> {
            com.pty4j.PtyProcess process = tab.getProcess();
            if (process != null) {
                process.onExit().thenAccept(p -> Platform.runLater(() -> {
                    tabPane.getTabs().remove(tab);
                    try {
                        tab.destroy();
                    } catch (Exception ignored) {}
                }));
            }
        });
    }

    /**
     * Toggle fullscreen mode. Notifies the parent layout via callback.
     */
    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        updateFullscreenIcon();
        if (onFullscreenToggle != null) {
            onFullscreenToggle.run();
        }
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    /**
     * Close all terminal tabs and destroy associated processes.
     */
    public void closeAllTabs() {
        // Close each tab (TerminalTab handles process cleanup internally)
        tabPane.getTabs().clear();
    }

    /**
     * Set the callback for fullscreen toggle events.
     */
    public void setOnFullscreenToggle(Runnable callback) {
        this.onFullscreenToggle = callback;
    }

    /**
     * Set the callback for hide/minimize events.
     */
    public void setOnHideRequest(Runnable callback) {
        this.onHideRequest = callback;
    }

    /**
     * Update dark/light mode for new terminals.
     */
    public void setDarkMode(boolean darkMode) {
        this.isDarkMode = darkMode;
    }

    /**
     * Returns true if there are active terminal tabs.
     */
    public boolean hasTabs() {
        return !tabPane.getTabs().isEmpty();
    }

    // --- Private Helpers ---

    private void updateFullscreenIcon() {
        if (fullscreenIcon != null) {
            if (isFullscreen) {
                // Restore/minimize icon
                fullscreenIcon.setContent("M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z");
                fullscreenButton.setTooltip(new Tooltip("Restore Terminal"));
            } else {
                // Maximize icon
                fullscreenIcon.setContent("M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z");
                fullscreenButton.setTooltip(new Tooltip("Maximize Terminal"));
            }
        }
    }
}
