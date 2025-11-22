/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.terraintoggle;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup(TerrainToggleConfig.GROUP)
public interface TerrainToggleConfig extends Config
{
    String GROUP = "terraintoggle";

    // -------------------------
    // MAIN SETTINGS
    // -------------------------

    @ConfigItem(
            keyName = "enableTerrain",
            name = "Display Terrain",
            description = "Enable or disable terrain visibility.",
            position = 1
    )
    default boolean enableTerrain() { return true; }

    @ConfigItem(
            keyName = "enableHotkey",
            name = "Enable Hotkey",
            description = "Allow the hotkey to toggle terrain on/off. (Controls Display Terrain Toggle)",
            position = 2
    )
    default boolean enableHotkey() { return false; }

    @ConfigItem(
            keyName = "toggleTerrainHotkey",
            name = "Terrain Toggle Hotkey",
            description = "Hotkey used to toggle terrain visibility on/off if hotkey is enabled.",
            position = 3
    )
    default Keybind toggleTerrainHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "enableRegionToggle",
            name = "Enable Region Auto-Toggle",
            description = "Automatically apply terrain rules based on selected regions.",
            position = 4
    )
    default boolean enableRegionToggle() { return false; }

    @ConfigItem(
            keyName = "enableRightClickRegion",
            name = "Enable Region Right-Click Menu",
            description = "Allows Shift-Right-Click to add/remove regions.",
            position = 5
    )
    default boolean enableRightClickRegion() { return false; }

    @ConfigItem(
            keyName = "notificationMode",
            name = "Notification Mode",
            description = "Choose how terrain toggle status is shown",
            position = 6
    )
    default NotificationMode notificationMode() { return NotificationMode.None; }

    // -----------------------------------------------------
    // REGION DATA / DEBUG SUBMENU
    // -----------------------------------------------------

    @ConfigSection(
            name = "Region Data / Debug",
            description = "Shows region IDs, lists, and region-related debug info.",
            position = 99,
            closedByDefault = true
    )
    String regionDataSection = "regionDataSection";

    @ConfigItem(
            keyName = "showRegions",
            name = "Show Regions",
            description = "Comma-separated list of region IDs where terrain will always be shown.",
            position = 100,
            section = regionDataSection
    )
    default String showRegions() { return ""; }

    @ConfigItem(
            keyName = "hideRegions",
            name = "Hide Regions",
            description = "Comma-separated list of region IDs where terrain will always be hidden.",
            position = 101,
            section = regionDataSection
    )
    default String hideRegions() { return ""; }

    @ConfigItem(
            keyName = "showRegionInOverlay",
            name = "Show Region in Overlay",
            description = "If enabled, the current region ID will always display in the overlay (if enabled).",
            position = 102,
            section = regionDataSection
    )
    default boolean showRegionInOverlay() { return false; }

    enum NotificationMode
    {
        None,
        Text,
        Overlay,
        Both
    }
}
