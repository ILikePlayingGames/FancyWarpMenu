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
    private static boolean showRegularWarpMenuButton;

    // Developer settings
    private static boolean debugModeEnabled;
    private static boolean showDebugOverlay;
    private static boolean drawBorders;
    private static boolean skipSkyBlockCheck;
    private static boolean alwaysShowJerryIsland;

    public static List<IConfigElement> getConfigElements() {
        List<IConfigElement> topLevelElements = new ArrayList<>();
        List<IConfigElement> generalElements = new ConfigElement(config.getCategory(CATEGORY_GENERAL)).getChildElements();
        List<IConfigElement> debugElements = new ConfigElement(config.getCategory(CATEGORY_DEBUG)).getChildElements();

        DummyConfigElement supportLinkElement = new DummyConfigElement("supportLink", EnvironmentDetails.SUPPORT_LINK, ConfigGuiType.CONFIG_CATEGORY, "fancywarpmenu.config.categories.support");
        supportLinkElement.setConfigEntryClass(FancyWarpMenuConfigScreen.OpenLinkEntry.class);

        topLevelElements.add(new DummyConfigElement.DummyCategoryElement(CATEGORY_GENERAL, "fancywarpmenu.config.categories.general", generalElements));
        topLevelElements.add(new DummyConfigElement.DummyCategoryElement(CATEGORY_DEBUG, "fancywarpmenu.config.categories.developerSettings", debugElements));
        topLevelElements.add(supportLinkElement);

        ForgeVersion.CheckResult updateCheckResult = FancyWarpMenu.getUpdateCheckResult();

        if (Settings.isUpdateNotificationEnabled() && updateCheckResult != null && (updateCheckResult.status == ForgeVersion.Status.OUTDATED || updateCheckResult.status == ForgeVersion.Status.BETA_OUTDATED)) {
            DummyConfigElement updateAvailableCategory = new DummyConfigElement("updateAvailable", null, ConfigGuiType.CONFIG_CATEGORY, "fancywarpmenu.config.categories.updateAvailable");
            Object[] options = {updateCheckResult.url, EnumChatFormatting.GREEN};
            updateAvailableCategory.set(options);
            updateAvailableCategory.setConfigEntryClass(FancyWarpMenuConfigScreen.ColoredOpenLinkEntry.class);
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

        /* General settings */
        prop = config.get(CATEGORY_GENERAL, "warpMenuEnabled", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.warpMenuEnabled"));
        warpMenuEnabled = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "showIslandLabels", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.showIslandLabels"));
        showIslandLabels = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "hideWarpLabelsUntilIslandHovered", false);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.hideWarpLabelsUntilIslandHovered"));
        hideWarpLabelsUntilIslandHovered = prop.getBoolean(false);

        prop = config.get(CATEGORY_GENERAL, "suggestWarpMenuOnWarpCommand", false);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.suggestWarpMenuOnWarpCommand"));
        suggestWarpMenuOnWarpCommand = prop.getBoolean(false);

        prop = config.get(CATEGORY_GENERAL, "addWarpCommandToChatHistory", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.addWarpCommandToChatHistory"));
        addWarpCommandToChatHistory = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "showJerryIsland", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.showJerryIsland"));
        showJerryIsland = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "hideUnobtainableWarps", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.hideUnobtainableWarps"));
        hideUnobtainableWarps = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "enableUpdateNotification", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.enableUpdateNotification"));
        enableUpdateNotification = prop.getBoolean(true);

        prop = config.get(CATEGORY_GENERAL, "showRegularWarpMenuButton", false);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.showRegularWarpMenuButton"));
        showRegularWarpMenuButton = prop.getBoolean(true);

        config.setCategoryRequiresWorldRestart(CATEGORY_GENERAL, false);

        /* Debug settings */
        prop = config.get(CATEGORY_DEBUG, "debugModeEnabled", false);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.developerModeEnabled"));
        debugModeEnabled = prop.getBoolean(false);

        if (!debugModeEnabled && EnvironmentDetails.isDeobfuscatedEnvironment()) {
            prop.set(true);
        }

        prop = config.get(CATEGORY_DEBUG, "showDebugOverlay", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.showDebugOverlay"));
        showDebugOverlay = prop.getBoolean(true);

        prop = config.get(CATEGORY_DEBUG, "drawBorders", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.drawBorders"));
        drawBorders = prop.getBoolean(true);

        prop = config.get(CATEGORY_DEBUG, "skipSkyBlockCheck", false);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.skipSkyBlockCheck"));
        skipSkyBlockCheck = prop.getBoolean(false);

        prop = config.get(CATEGORY_DEBUG, "alwaysShowJerryIsland", true);
        prop.setLanguageKey(FancyWarpMenu.getFullLanguageKey("config.alwaysShowJerryIsland"));
        alwaysShowJerryIsland = prop.getBoolean(true);

        config.setCategoryRequiresWorldRestart(CATEGORY_DEBUG, false);

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

    public static boolean shouldShowRegularWarpMenuButton() {
        return showRegularWarpMenuButton;
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

    public static boolean shouldAlwaysShowJerryIsland() {
        return alwaysShowJerryIsland;
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
