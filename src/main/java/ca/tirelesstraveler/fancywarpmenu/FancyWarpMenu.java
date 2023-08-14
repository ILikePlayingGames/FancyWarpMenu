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
import ca.tirelesstraveler.fancywarpmenu.data.Layout;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.listeners.SkyBlockJoinListener;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
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

@Mod(modid = "fancywarpmenu", clientSideOnly = true, useMetadata = true, guiFactory = "ca.tirelesstraveler.fancywarpmenu.gui.FancyWarpMenuGuiFactory")
public class FancyWarpMenu {
    @Mod.Instance("fancywarpmenu")
    private static FancyWarpMenu instance;
    private static ModContainer modContainer;
    private static String modId;
    static Logger logger;
    private static Layout layout;
    private static SkyBlockJoinListener skyblockJoinListener;
    private static WarpMenuListener warpMenuListener;
    private static KeyBinding keyBindingOpenWarpMenu;

    public static FancyWarpMenu getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ProgressManager.ProgressBar bar = ProgressManager.push("Pre-init", 3);
        EnvironmentDetails.deobfuscatedEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        modId = event.getModMetadata().modId;
        modContainer = Loader.instance().activeModContainer();
        bar.step("Initializing Listeners");
        warpMenuListener = new WarpMenuListener();
        MinecraftForge.EVENT_BUS.register(warpMenuListener);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(warpMenuListener);
        skyblockJoinListener = new SkyBlockJoinListener();
        MinecraftForge.EVENT_BUS.register(skyblockJoinListener);
        bar.step("Loading Settings");
        Settings.setConfig(new Configuration(event.getSuggestedConfigurationFile(), modContainer.getVersion()));
        Settings.setConfigPropertyOrder();
        Settings.syncConfig(true);
        logger = event.getModLog();
        event.getModMetadata().version = modContainer.getVersion();
        bar.step("Loading Warp Configuration");
        layout = LayoutLoader.loadLayout();
        ProgressManager.pop(bar);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        keyBindingOpenWarpMenu = new KeyBinding("fancywarpmenu.key.openMenu", Keyboard.KEY_C, "fancywarpmenu.key.categories.fancyWarpMenu");
        ClientRegistry.registerKeyBinding(keyBindingOpenWarpMenu);

        ProgressManager.ProgressBar bar = ProgressManager.push("Loading Textures", layout.getIslandList().size() + 1);
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        for (Island island : layout.getIslandList()) {
            bar.step(island.getName());
            textureManager.bindTexture(island.getTextureLocation());
        }

        bar.step("Warp Icon");
        textureManager.bindTexture(layout.getWarpIcon().getTextureLocation());

        ProgressManager.pop(bar);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("patcher")) {
            EnvironmentDetails.patcherInstalled = true;
        }
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

    public WarpMenuListener getWarpMenuListener() {
        return warpMenuListener;
    }

    public boolean isPlayerOnSkyBlock() {
        return skyblockJoinListener.isOnSkyBlock();
    }

    public void reloadResources() {
        Minecraft.getMinecraft().refreshResources();
        reloadLayout();
    }

    public void reloadLayout() {
        Layout loadedLayout = LayoutLoader.loadLayout();

        // Will be null if json syntax is wrong or config is invalid
        if (loadedLayout != null) {
            FancyWarpMenu.layout = loadedLayout;
        }
    }

    public static KeyBinding getKeyBindingOpenWarpMenu() {
        return keyBindingOpenWarpMenu;
    }

    public static Layout getLayout() {
        return layout;
    }
}
