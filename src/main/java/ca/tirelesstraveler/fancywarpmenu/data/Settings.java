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

package ca.tirelesstraveler.fancywarpmenu.data;

import ca.tirelesstraveler.fancywarpmenu.EnvironmentDetails;
import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.gui.FancyWarpMenuConfigScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Settings {
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DEBUG = "debug";
    private static final String CATEGORY_UPDATE_AVAILABLE = "updateAvailable";

    private static Configuration config;
    // General settings
    private static boolean warpMenuEnabled;
    private static boolean showIslandLabels;
    private static boolean hideWarpLabelsUntilIslandHovered;
    private static boolean suggestWarpMenuOnWarpCommand;
    private static boolean addWarpCommandToChatHistory;
    private static boolean showJerryIsland;
    private static boolean hideUnobtainableWarps;
    private static boolean enableUpdateNotification;

    // Developer settings
    private static boolean debugModeEnabled;
    private static boolean showDebugOverlay;
    private static boolean drawBorders;
    private static boolean skipSkyBlockCheck;

    public static List<IConfigElement> getConfigElements() {
        List<IConfigElement> topLevelElements = new ArrayList<>();
        List<IConfigElement> generalElements = new ConfigElement(config.getCategory(CATEGORY_GENERAL)).getChildElements();
        List<IConfigElement> debugElements = new ConfigElement(config.getCategory(CATEGORY_DEBUG)).getChildElements();

        DummyConfigElement supportLinkElement = new DummyConfigElement("supportLink", EnvironmentDetails.SUPPORT_LINK, ConfigGuiType.STRING, "fancywarpmenu.config.categories.support");
        supportLinkElement.setConfigEntryClass(FancyWarpMenuConfigScreen.OpenLinkEntry.class);

        topLevelElements.add(new DummyConfigElement.DummyCategoryElement(CATEGORY_GENERAL, "fancywarpmenu.config.categories.general", generalElements));
        topLevelElements.add(new DummyConfigElement.DummyCategoryElement(CATEGORY_DEBUG, "fancywarpmenu.config.categories.developerSettings", debugElements));
        topLevelElements.add(supportLinkElement);

        ForgeVersion.CheckResult updateCheckResult = FancyWarpMenu.getUpdateCheckResult();

        if (Settings.isUpdateNotificationEnabled() && updateCheckResult != null && updateCheckResult.status == ForgeVersion.Status.OUTDATED) {
            List<IConfigElement> updateAvailableElements = new ArrayList<>();

            DummyConfigElement currentVersionElement = new DummyConfigElement("currentVersion", FancyWarpMenu.getInstance().getModContainer().getVersion(), ConfigGuiType.STRING, "fancywarpmenu.config.currentVersion");
            DummyConfigElement newVersionElement = new DummyConfigElement("newVersion", updateCheckResult.target.toString(), ConfigGuiType.STRING, "fancywarpmenu.config.newVersion");
            DummyConfigElement downloadUpdateElement = new DummyConfigElement("downloadUpdate", EnvironmentDetails.SUPPORT_LINK, ConfigGuiType.STRING, "fancywarpmenu.config.downloadUpdate");
            DummyConfigElement updateAvailableCategory = new DummyConfigElement.DummyCategoryElement(CATEGORY_UPDATE_AVAILABLE, "fancywarpmenu.config.categories.updateAvailable", updateAvailableElements);

            currentVersionElement.setConfigEntryClass(FancyWarpMenuConfigScreen.UnmodifiableStringEntry.class);
            newVersionElement.setConfigEntryClass(FancyWarpMenuConfigScreen.UnmodifiableStringEntry.class);
            downloadUpdateElement.setConfigEntryClass(FancyWarpMenuConfigScreen.OpenLinkEntry.class);
            updateAvailableCategory.set(EnumChatFormatting.GREEN);
            updateAvailableCategory.setConfigEntryClass(FancyWarpMenuConfigScreen.ColoredCategoryEntry.class);

            updateAvailableElements.add(currentVersionElement);
            updateAvailableElements.add(newVersionElement);
            updateAvailableElements.add(downloadUpdateElement);

            topLevelElements.add(updateAvailableCategory);
        }

        return topLevelElements;
    }

    public static void setConfig(Configuration config) {
        Settings.config = config;
    }

    /**
     * Sets the order in which config properties are displayed on the settings menu
     */
    public static void setConfigPropertyOrder() {
        List<String> generalPropertyOrder = new ArrayList<>();
        Collections.addAll(generalPropertyOrder, "warpMenuEnabled", "showIslandLabels", "hideWarpLabelsUntilIslandHovered", "suggestWarpMenuOnWarpCommand", "addWarpCommandToChatHistory", "showJerryIsland", "hideUnobtainableWarps", "enableUpdateNotification");

        List<String> debugPropertyOrder = new ArrayList<>();
        Collections.addAll(debugPropertyOrder, "debugModeEnabled", "showDebugOverlay", "drawBorders", "skipSkyBlockCheck");

        config.setCategoryPropertyOrder(CATEGORY_GENERAL, generalPropertyOrder);
        config.setCategoryPropertyOrder(CATEGORY_DEBUG, debugPropertyOrder);
    }

    public static void syncConfig(boolean load) {
        if (load) {
            config.load();
        }

        Property prop;

        prop = config.get(CATEGORY_GENERAL, "warpMenuEnabled", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.warpMenuEnabled"));
        prop.setRequiresWorldRestart(false);
        warpMenuEnabled = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "showIslandLabels", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.showIslandLabels"));
        prop.setRequiresWorldRestart(false);
        showIslandLabels = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "hideWarpLabelsUntilIslandHovered", false);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.hideWarpLabelsUntilIslandHovered"));
        prop.setRequiresWorldRestart(false);
        hideWarpLabelsUntilIslandHovered = prop.getBoolean(false);

        prop = config.get(CATEGORY_GENERAL, "suggestWarpMenuOnWarpCommand", false);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.suggestWarpMenuOnWarpCommand"));
        prop.setRequiresWorldRestart(false);
        suggestWarpMenuOnWarpCommand = prop.getBoolean(false);

        prop = config.get(CATEGORY_GENERAL, "addWarpCommandToChatHistory", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.addWarpCommandToChatHistory"));
        prop.setRequiresWorldRestart(false);
        addWarpCommandToChatHistory = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "showJerryIsland", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.showJerryIsland"));
        prop.setRequiresWorldRestart(false);
        showJerryIsland = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "hideUnobtainableWarps", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.hideUnobtainableWarps"));
        prop.setRequiresWorldRestart(false);
        hideUnobtainableWarps = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "enableUpdateNotification", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.enableUpdateNotification"));
        prop.setRequiresWorldRestart(false);
        prop.getBoolean(true);
        enableUpdateNotification = prop.getBoolean(true);

        /* Debug settings */

        prop = config.get(CATEGORY_DEBUG, "debugModeEnabled", false);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.developerModeEnabled"));
        prop.setRequiresWorldRestart(false);
        debugModeEnabled = prop.getBoolean(false);

        if (!debugModeEnabled && EnvironmentDetails.isDeobfuscatedEnvironment()) {
            prop.set(true);
        }

        prop = config.get(CATEGORY_DEBUG, "showDebugOverlay", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.showDebugOverlay"));
        prop.setRequiresWorldRestart(false);
        showDebugOverlay = prop.getBoolean(true);

        prop = config.get(CATEGORY_DEBUG, "drawBorders", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.drawBorders"));
        prop.setRequiresWorldRestart(false);
        drawBorders = prop.getBoolean(true);

        prop = config.get(CATEGORY_DEBUG, "skipSkyBlockCheck", false);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.skipSkyBlockCheck"));
        prop.setRequiresWorldRestart(false);
        skipSkyBlockCheck = prop.getBoolean(false);

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static boolean isWarpMenuEnabled() {
        return warpMenuEnabled;
    }

    public static boolean shouldShowIslandLabels() {
        return showIslandLabels;
    }

    public static boolean shouldHideWarpLabelsUntilIslandHovered() {
        return hideWarpLabelsUntilIslandHovered;
    }

    public static boolean shouldSuggestWarpMenuOnWarpCommand() {
        return suggestWarpMenuOnWarpCommand;
    }

    public static boolean shouldAddWarpCommandToChatHistory() {
        return addWarpCommandToChatHistory;
    }

    public static boolean shouldShowJerryIsland() {
        return showJerryIsland;
    }

    public static boolean shouldHideUnobtainableWarps() {
        return hideUnobtainableWarps;
    }

    public static boolean isUpdateNotificationEnabled() {
        return enableUpdateNotification;
    }

    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public static boolean shouldShowDebugOverlay() {
        return showDebugOverlay;
    }

    public static boolean shouldDrawBorders() {
        return drawBorders;
    }

    public static boolean shouldSkipSkyBlockCheck() {
        return skipSkyBlockCheck;
    }

    public static void setShowDebugOverlay(boolean showDebugOverlay) {
        config.get(CATEGORY_DEBUG, "showDebugOverlay", true).set(showDebugOverlay);
        syncConfig(false);
    }

    public static void setDrawBorders(boolean drawBorders) {
        config.get(CATEGORY_DEBUG, "drawBorders", true).set(drawBorders);
        syncConfig(false);
    }

    public static void setWarpMenuEnabled(boolean warpMenuEnabled) {
        config.get(CATEGORY_GENERAL, "warpMenuEnabled", true).set(warpMenuEnabled);
        syncConfig(false);
    }
}
