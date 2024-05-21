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

import ca.tirelesstraveler.fancywarpmenu.commands.FancyWarpMenuCommand;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Island;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Layout;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.listeners.ChatListener;
import ca.tirelesstraveler.fancywarpmenu.resourceloaders.LayoutLoader;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.SkyBlockConstants;
import ca.tirelesstraveler.fancywarpmenu.listeners.SkyBlockJoinListener;
import ca.tirelesstraveler.fancywarpmenu.listeners.WarpMenuListener;
import ca.tirelesstraveler.fancywarpmenu.resourceloaders.SkyBlockConstantsLoader;
import ca.tirelesstraveler.fancywarpmenu.state.EnvironmentDetails;
import ca.tirelesstraveler.fancywarpmenu.state.FancyWarpMenuState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.ForgeVersion;
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

@Mod(modid = "fancywarpmenu", clientSideOnly = true, useMetadata = true, guiFactory = "ca.tirelesstraveler.fancywarpmenu.gui.FancyWarpMenuGuiFactory", updateJSON = "https://cdn.jsdelivr.net/gh/ILikePlayingGames/FancyWarpMenu@main/version/update.json")
public class FancyWarpMenu {
    @Mod.Instance("fancywarpmenu")
    private static FancyWarpMenu instance;
    private static ModContainer modContainer;
    private static String modId;
    static Logger logger;
    private static ForgeVersion.CheckResult updateCheckResult;
    private static SkyBlockConstants skyBlockConstants;
    private static KeyBinding keyBindingOpenWarpMenu;

    public static FancyWarpMenu getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ProgressManager.ProgressBar bar = ProgressManager.push("Pre-init", 5);
        EnvironmentDetails.setDeobfuscatedEnvironment((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));
        modId = event.getModMetadata().modId;
        modContainer = Loader.instance().activeModContainer();
        bar.step("Initializing Listeners");
        WarpMenuListener warpMenuListener = new WarpMenuListener();
        MinecraftForge.EVENT_BUS.register(warpMenuListener);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(warpMenuListener);
        SkyBlockJoinListener skyblockJoinListener = new SkyBlockJoinListener();
        MinecraftForge.EVENT_BUS.register(skyblockJoinListener);
        ChatListener chatListener = new ChatListener();
        MinecraftForge.EVENT_BUS.register(chatListener);
        bar.step("Loading Settings");
        Settings.setConfig(new Configuration(event.getSuggestedConfigurationFile(), modContainer.getVersion()));
        Settings.setConfigPropertyOrder();
        Settings.syncConfig(true);
        logger = event.getModLog();
        event.getModMetadata().version = modContainer.getVersion();
        bar.step("Loading SkyBlock Constants");
        skyBlockConstants = SkyBlockConstantsLoader.loadSkyBlockConstants();
        bar.step("Loading Layout");
        FancyWarpMenuState.setOverworldLayout(LayoutLoader.loadLayout(LayoutLoader.OVERWORLD_LAYOUT_LOCATION));
        bar.step("Loading Rift Layout");
        FancyWarpMenuState.setRiftLayout(LayoutLoader.loadLayout(LayoutLoader.RIFT_LAYOUT_LOCATION));
        ProgressManager.pop(bar);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        updateCheckResult = ForgeVersion.getResult(modContainer);
        keyBindingOpenWarpMenu = new KeyBinding("fancywarpmenu.key.openMenu", Keyboard.KEY_C, "fancywarpmenu.key.categories.fancyWarpMenu");
        ClientRegistry.registerKeyBinding(keyBindingOpenWarpMenu);
        ClientCommandHandler.instance.registerCommand(new FancyWarpMenuCommand());

        Layout overworldLayout = FancyWarpMenuState.getOverworldLayout();
        ProgressManager.ProgressBar bar = ProgressManager.push("Loading Textures",
                overworldLayout.getIslandList().size() + 1);
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        for (Island island : overworldLayout.getIslandList()) {
            bar.step(island.getName());
            textureManager.bindTexture(island.getTextureLocation());
        }

        bar.step("Warp Icon");
        textureManager.bindTexture(overworldLayout.getWarpIcon().getTextureLocation());

        ProgressManager.pop(bar);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        EnvironmentDetails.setPatcherInstalled(Loader.isModLoaded("patcher"));
    }

    public ModContainer getModContainer() {
        return modContainer;
    }

    public String getModId() {
        return modId;
    }

    public static ForgeVersion.CheckResult getUpdateCheckResult() {
        return updateCheckResult;
    }

    public void reloadResources() {
        Minecraft.getMinecraft().refreshResources();
        reloadSkyBlockConstants();
        reloadLayouts();
    }

    public void reloadSkyBlockConstants() {
        SkyBlockConstants loadedSkyBlockConstants = SkyBlockConstantsLoader.loadSkyBlockConstants();

        // Will be null if json syntax is wrong or SkyBlock constants are invalid
        if (loadedSkyBlockConstants != null) {
            FancyWarpMenu.skyBlockConstants = loadedSkyBlockConstants;
        }
    }

    public void reloadLayouts() {
        Layout loadedOverworldLayout = LayoutLoader.loadLayout(LayoutLoader.OVERWORLD_LAYOUT_LOCATION);
        Layout loadedRiftLayout = LayoutLoader.loadLayout(LayoutLoader.RIFT_LAYOUT_LOCATION);

        // Will be null if json syntax is wrong or layout is invalid
        if (loadedOverworldLayout != null) {
            FancyWarpMenuState.setOverworldLayout(loadedOverworldLayout);
        }

        if (loadedRiftLayout != null) {
            FancyWarpMenuState.setRiftLayout(loadedRiftLayout);
        }
    }

    /**
     * Returns the given language key path with the mod ID prepended
     */
    public static String getFullLanguageKey(String path) {
        return modId + "." + path;
    }

    public static KeyBinding getKeyBindingOpenWarpMenu() {
        return keyBindingOpenWarpMenu;
    }

    public static SkyBlockConstants getSkyBlockConstants() {
        return skyBlockConstants;
    }
}
