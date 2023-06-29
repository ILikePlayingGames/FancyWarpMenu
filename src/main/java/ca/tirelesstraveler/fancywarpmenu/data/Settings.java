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

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class Settings {
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DEBUG = "debug";

    private static Configuration config;
    // General settings
    private static boolean warpMenuEnabled;
    private static boolean showIslandLabels;
    private static boolean hideWarpLabelsUntilIslandHovered;
    private static boolean remindToUse;
    // Developer settings
    private static boolean debugModeEnabled;
    private static boolean showDebugOverlay;
    private static boolean drawBorders;

    public static List<IConfigElement> getConfigElements() {
        List<IConfigElement> topLevelElements = new ConfigElement(config.getCategory(CATEGORY_GENERAL)).getChildElements();
        List<IConfigElement> debugElements = new ConfigElement(config.getCategory(CATEGORY_DEBUG)).getChildElements();

        topLevelElements.add(new DummyConfigElement.DummyCategoryElement(CATEGORY_DEBUG, "fancywarpmenu.config.categories.developerSettings", debugElements));

        return topLevelElements;
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

        prop = config.get(CATEGORY_GENERAL, "remindToUse", false);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.remindToUse"));
        prop.setRequiresWorldRestart(false);
        remindToUse = prop.getBoolean(false);

        prop = config.get(CATEGORY_DEBUG, "debugModeEnabled", false);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.developerModeEnabled"));
        prop.setRequiresWorldRestart(false);
        debugModeEnabled = prop.getBoolean(false);

        prop = config.get(CATEGORY_DEBUG, "showDebugOverlay", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.showDebugOverlay"));
        prop.setRequiresWorldRestart(false);
        showDebugOverlay = prop.getBoolean(true);

        prop = config.get(CATEGORY_DEBUG, "drawBorders", true);
        prop.setLanguageKey(FancyWarpMenu.getInstance().getFullLanguageKey("config.drawBorders"));
        prop.setRequiresWorldRestart(false);
        drawBorders = prop.getBoolean(true);

        if (!debugModeEnabled && Boolean.getBoolean("fancywarpmenu.debugRendering")) {
            prop.set(true);
        }

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

    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public static boolean shouldShowDebugOverlay() {
        return showDebugOverlay;
    }

    public static boolean shouldDrawBorders() {
        return drawBorders;
    }

    public static void setShowDebugOverlay(boolean showDebugOverlay) {
        config.get(CATEGORY_DEBUG, "showDebugOverlay", true).set(showDebugOverlay);
        syncConfig(false);
    }

    public static void setDrawBorders(boolean drawBorders) {
        config.get(CATEGORY_DEBUG, "drawBorders", true).set(drawBorders);
        syncConfig(false);
    }

    public static boolean shouldRemindToUse() {
        return remindToUse;
    }
}
