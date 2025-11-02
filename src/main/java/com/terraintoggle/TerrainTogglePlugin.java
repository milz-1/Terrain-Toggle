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

import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
        name = "Terrain Toggle",
        description = "Toggle terrain visibility on or off, requires 117 HD / GPU",
        tags = {"terrain", "toggle", "floor", "ground", "remover", "blind", "accessibility"}
)
public class TerrainTogglePlugin extends Plugin implements DrawCallbacks, KeyListener
{
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private TerrainToggleConfig config;
    @Inject private ConfigManager configManager;
    @Inject private KeyManager keyManager;

    private DrawCallbacks interceptedDrawCallbacks;
    private boolean terrainVisible = true;
    private boolean consumeKeys = false;

    @Provides
    TerrainToggleConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(TerrainToggleConfig.class);
    }

    @Override
    protected void startUp()
    {
        log.info("TerrainToggle started");
        keyManager.registerKeyListener(this);
        terrainVisible = config.enableTerrain();
        clientThread.invoke(this::interceptDrawCallbacks);
    }

    @Override
    protected void shutDown()
    {
        log.info("TerrainToggle stopped");
        keyManager.unregisterKeyListener(this);
        clientThread.invoke(() -> {
            if (client.getDrawCallbacks() == this && interceptedDrawCallbacks != null)
                client.setDrawCallbacks(interceptedDrawCallbacks);
            interceptedDrawCallbacks = null;
        });
    }

    private boolean interceptDrawCallbacks()
    {
        if (!client.isGpu())
        {
            log.warn("GPU plugin must be enabled for TerrainToggle to work");
            return false;
        }

        DrawCallbacks current = client.getDrawCallbacks();
        if (current == null || current == this)
            return false;

        interceptedDrawCallbacks = current;
        client.setDrawCallbacks(this);
        log.info("DrawCallbacks intercepted");
        return true;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals(TerrainToggleConfig.GROUP))
        {
            terrainVisible = config.enableTerrain();
            log.info("Terrain visibility changed via config: {}", terrainVisible);
        }
    }

    // === Hotkey handling ===
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.toggleTerrainHotkey().matches(e))
        {
            consumeKeys = true;
            e.consume();

            // Toggle terrain visibility
            boolean newState = !config.enableTerrain();
            configManager.setConfiguration(TerrainToggleConfig.GROUP, "enableTerrain", newState);
            terrainVisible = newState;
            log.info("Terrain visibility toggled via hotkey: {}", newState);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (config.toggleTerrainHotkey().matches(e))
        {
            consumeKeys = false;
            e.consume();
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        if (consumeKeys)
        {
            e.consume();
        }
    }

    // === DrawCallbacks ===
    @Override
    public void drawScenePaint(Scene scene, SceneTilePaint paint, int plane, int tileX, int tileY)
    {
        if (terrainVisible && interceptedDrawCallbacks != null)
            interceptedDrawCallbacks.drawScenePaint(scene, paint, plane, tileX, tileY);
    }

    @Override
    public void drawSceneTileModel(Scene scene, SceneTileModel model, int tileX, int tileY)
    {
        if (terrainVisible && interceptedDrawCallbacks != null)
            interceptedDrawCallbacks.drawSceneTileModel(scene, model, tileX, tileY);
    }

    // === Required DrawCallbacks stubs ===
    @Override public void drawScene(double cameraX, double cameraY, double cameraZ, double cameraPitch, double cameraYaw, int plane) { if (interceptedDrawCallbacks != null) interceptedDrawCallbacks.drawScene(cameraX, cameraY, cameraZ, cameraPitch, cameraYaw, plane); }
    @Override public void postDrawScene() { if (interceptedDrawCallbacks != null) interceptedDrawCallbacks.postDrawScene(); }
    @Override public void animate(Texture texture, int diff) { if (interceptedDrawCallbacks != null) interceptedDrawCallbacks.animate(texture, diff); }
    @Override public void loadScene(Scene scene) { if (interceptedDrawCallbacks != null) interceptedDrawCallbacks.loadScene(scene); }
    @Override public void swapScene(Scene scene) { if (interceptedDrawCallbacks != null) interceptedDrawCallbacks.swapScene(scene); }
    @Override public boolean tileInFrustum(Scene scene, float pitchSin, float pitchCos, float yawSin, float yawCos, int cameraX, int cameraY, int cameraZ, int plane, int msx, int msy) { return interceptedDrawCallbacks == null || interceptedDrawCallbacks.tileInFrustum(scene, pitchSin, pitchCos, yawSin, yawCos, cameraX, cameraY, cameraZ, plane, msx, msy); }
    @Override public void draw(int overlayColor) { if (interceptedDrawCallbacks != null) interceptedDrawCallbacks.draw(overlayColor); }
    @Override public void draw(Projection projection, Scene scene, Renderable renderable, int orientation, int x, int y, int z, long hash) { if (interceptedDrawCallbacks != null) interceptedDrawCallbacks.draw(projection, scene, renderable, orientation, x, y, z, hash); }
}
