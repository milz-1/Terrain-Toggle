package com.terraintoggle;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class TerrainToggleOverlay extends Overlay {

    private final TerrainTogglePlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    // Initial width for the overlay box
    private static final int INITIAL_WIDTH = 140;
    private static final int MIN_HEIGHT = 150;

    @Inject
    public TerrainToggleOverlay(TerrainTogglePlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);  // Position overlay in top left
        setLayer(OverlayLayer.ABOVE_WIDGETS);   // Ensure it's above other UI elements

        // Set the initial size of the overlay (modifiable by user)
        panelComponent.setPreferredSize(new Dimension(INITIAL_WIDTH, MIN_HEIGHT));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Check the notification mode to decide whether to show the overlay
        TerrainToggleConfig.NotificationMode notificationMode = plugin.getConfig().notificationMode();

        // Do not render overlay if notification mode is NONE or TEXT
        if (notificationMode == TerrainToggleConfig.NotificationMode.None || notificationMode == TerrainToggleConfig.NotificationMode.Text) {
            return null; // Skip rendering the overlay entirely
        }

        // Only show overlay if notification mode is OVERLAY or BOTH
        panelComponent.getChildren().clear();  // Clear any previous content

        // === Title: "Terrain Toggle" ===
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Terrain Toggle")
                .color(Color.CYAN)
                .build());

        // === Master Toggle (Terrain Visibility) ===
        panelComponent.getChildren().add(createLineComponent("Terrain", plugin.isTerrainVisible() ? "ON" : "OFF", plugin.isTerrainVisible()));

        // === Hotkey Mode ===
        panelComponent.getChildren().add(createLineComponent("Hotkey Mode", plugin.isHotkeyEnabled() ? "ON" : "OFF", plugin.isHotkeyEnabled()));

        // === Region Mode ===
        panelComponent.getChildren().add(createLineComponent("Region Mode", plugin.isRegionToggleEnabled() ? "ON" : "OFF", plugin.isRegionToggleEnabled()));

        // === Current Region ID ===
        int currentRegion = plugin.getCurrentRegionId();
        if (currentRegion != -1 && plugin.getConfig().showRegionInOverlay()) {
            panelComponent.getChildren().add(createLineComponent("Current Region", String.valueOf(currentRegion), true));
        }

        // === Highlight if in Selected Region ===
        if (plugin.isInSelectedRegion()) {
            panelComponent.getChildren().add(createLineComponent("In Listed Region", "Yes", true));
        }

        // Set a flexible width for the panel to allow resizing
        panelComponent.setPreferredSize(new Dimension(INITIAL_WIDTH, panelComponent.getPreferredSize().height));

        return panelComponent.render(graphics); // Render the updated overlay content
    }

    // Helper method to create a formatted line with a label and status
    private LineComponent createLineComponent(String label, String status, boolean isActive) {
        Color statusColor = isActive ? Color.GREEN : Color.RED;
        return LineComponent.builder()
                .left(label)  // Left side of the row
                .right(status)  // Right side of the row (ON/OFF, etc.)
                .leftColor(Color.WHITE)
                .rightColor(statusColor)
                .build();
    }

    //update the overlay message
    public void updateOverlayMessage(String message) {
        // Clear previous messages (if any) and set the new message
        panelComponent.getChildren().clear(); // Clears any old components

        // Add the new message to the panel
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(message)
                .color(Color.WHITE)
                .build());
    }
}
