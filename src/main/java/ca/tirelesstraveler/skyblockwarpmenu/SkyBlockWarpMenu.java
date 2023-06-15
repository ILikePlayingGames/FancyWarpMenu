package ca.tirelesstraveler.skyblockwarpmenu;

import ca.tirelesstraveler.skyblockwarpmenu.data.Island;
import ca.tirelesstraveler.skyblockwarpmenu.data.Settings;
import ca.tirelesstraveler.skyblockwarpmenu.data.WarpConfiguration;
import ca.tirelesstraveler.skyblockwarpmenu.listeners.SkyBlockJoinListener;
import ca.tirelesstraveler.skyblockwarpmenu.listeners.WarpMenuListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(modid = "skyblockwarpmenu", clientSideOnly = true, guiFactory = "ca.tirelesstraveler.skyblockwarpmenu.gui.SWMGuiFactory")
public class SkyBlockWarpMenu {
    @Mod.Instance("skyblockwarpmenu")
    private static SkyBlockWarpMenu instance;
    private static ModContainer modContainer;
    private static String modId;
    private static WarpConfiguration warpConfig;
    private static SkyBlockJoinListener skyblockJoinListener;
    private static WarpMenuListener warpMenuListener;
    private static Logger logger;

    public static SkyBlockWarpMenu getInstance() {
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
        Settings.syncConfig(true);
        logger = event.getModLog();
        event.getModMetadata().version = modContainer.getVersion();
        bar.step("Loading Warp Configuration");
        warpConfig = WarpConfigLoader.loadIslands();

        if (warpConfig == null) {
            throw new RuntimeException("Error loading warp configuration");
        }
        ProgressManager.pop(bar);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
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

    public boolean isPlayerOnSkyblock() {
        return skyblockJoinListener.isOnSkyBlock();
    }

    public void reloadResources() {
        Minecraft.getMinecraft().refreshResources();
        WarpConfiguration warpConfig = WarpConfigLoader.loadIslands();

        // Will be null if json syntax is wrong
        if (warpConfig != null) {
            SkyBlockWarpMenu.warpConfig = warpConfig;
        }
    }
}
