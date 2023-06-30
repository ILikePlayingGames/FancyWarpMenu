/*
 * Copyright (c) 2023. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.tirelesstraveler.fancywarpmenu;

import ca.tirelesstraveler.fancywarpmenu.data.Island;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.WarpConfiguration;
import ca.tirelesstraveler.fancywarpmenu.data.WarpMessages;
import ca.tirelesstraveler.fancywarpmenu.listeners.SkyBlockJoinListener;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.List;

@Mod(modid = "fancywarpmenu", clientSideOnly = true, guiFactory = "ca.tirelesstraveler.fancywarpmenu.gui.FancyWarpMenuGuiFactory")
public class FancyWarpMenu {
    @Mod.Instance("fancywarpmenu")
    private static FancyWarpMenu instance;
    private static ModContainer modContainer;
    private static String modId;
    private static WarpConfiguration warpConfig;
    private static SkyBlockJoinListener skyblockJoinListener;
    private static WarpMenuListener warpMenuListener;
    private static KeyBinding keyBindingOpenWarpMenu;
    private static Logger logger;

    public static FancyWarpMenu getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ProgressManager.ProgressBar bar = ProgressManager.push("Pre-init", 3);

        modId = event.getModMetadata().modId;
        modContainer = Loader.instance().activeModContainer();
        bar.step("Initializing Listeners");
        warpMenuListener = new WarpMenuListener();
        MinecraftForge.EVENT_BUS.register(warpMenuListener);
        skyblockJoinListener = new SkyBlockJoinListener(warpMenuListener);
        MinecraftForge.EVENT_BUS.register(skyblockJoinListener);
        bar.step("Loading Settings");
        Settings.setConfig(new Configuration(event.getSuggestedConfigurationFile(), modContainer.getVersion()));
        Settings.setConfigPropertyOrder();
        Settings.syncConfig(true);
        logger = event.getModLog();
        event.getModMetadata().version = modContainer.getVersion();
        bar.step("Loading Warp Configuration");
        warpConfig = WarpConfigLoader.loadIslands();
        ProgressManager.pop(bar);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        keyBindingOpenWarpMenu = new KeyBinding("fancywarpmenu.key.openMenu", Keyboard.KEY_C, "fancywarpmenu.key.categories.fancyWarpMenu");
        ClientRegistry.registerKeyBinding(keyBindingOpenWarpMenu);

        ProgressManager.ProgressBar bar = ProgressManager.push("Loading Textures", getIslands().size() + 1);
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        for (Island island:
                getIslands()) {
            bar.step(island.getName());
            textureManager.bindTexture(island.getTextureLocation());
        }

        bar.step("Warp Icon");
        textureManager.bindTexture(warpConfig.getWarpIcon().getTextureLocation());

        ProgressManager.pop(bar);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    public ModContainer getModContainer() {
        return modContainer;
    }

    public String getModId() {
        return modId;
    }

    /**
     * Returns the given language key path with the mod ID prepended
     */
    public String getFullLanguageKey(String path) {
        return modId + "." + path;
    }

    public List<Island> getIslands() {
        return warpConfig.getIslandList();
    }

    public static KeyBinding getKeyBindingOpenWarpMenu() {
        return keyBindingOpenWarpMenu;
    }

    public WarpMessages getWarpMessages() {
        return  warpConfig.getWarpMessages();
    }

    public boolean isPlayerOnSkyBlock() {
        return skyblockJoinListener.isOnSkyBlock();
    }

    public void reloadResources() {
        Minecraft.getMinecraft().refreshResources();
        WarpConfiguration warpConfig = WarpConfigLoader.loadIslands();

        // Will be null if json syntax is wrong or config is invalid
        if (warpConfig != null) {
            FancyWarpMenu.warpConfig = warpConfig;
        }
    }
}
