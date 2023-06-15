package ca.tirelesstraveler.skyblockwarpmenu.data;

import ca.tirelesstraveler.skyblockwarpmenu.SkyBlockWarpMenu;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class Settings {
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DEBUG = "debug";

    private static Configuration config;
    private static boolean warpMenuEnabled;
    private static boolean showIslandLabels;
    private static boolean debugModeEnabled;

    public static List<IConfigElement> getConfigElements() {
        return new ConfigElement(config.getCategory(CATEGORY_GENERAL)).getChildElements();
    }

    public static void setConfig(Configuration config) {
        Settings.config = config;
    }

    public static void syncConfig(boolean load) {
        if (load) {
            config.load();
        }

        Property prop;

        prop = config.get(CATEGORY_GENERAL, "warpMenuEnabled", true);
        prop.setLanguageKey(SkyBlockWarpMenu.getInstance().getFullLanguageKey("config.warpMenuEnabled"));
        prop.setRequiresWorldRestart(false);
        warpMenuEnabled = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "showIslandLabels", true);
        prop.setLanguageKey(SkyBlockWarpMenu.getInstance().getFullLanguageKey("config.showIslandLabels"));
        prop.setRequiresWorldRestart(false);
        showIslandLabels = prop.getBoolean(true);

        prop = config.get(CATEGORY_DEBUG, "debugModeEnabled", false);
        prop.setShowInGui(false);
        prop.setRequiresWorldRestart(false);
        debugModeEnabled = prop.getBoolean(false) || Boolean.getBoolean("skyblockwarpmenu.debugRendering");

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static boolean isWarpMenuEnabled() {
        return warpMenuEnabled;
    }

    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public static boolean shouldShowIslandLabels() {
        return showIslandLabels;
    }

    private Settings() {

    }
}
