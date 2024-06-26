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

package ca.tirelesstraveler.fancywarpmenu.state;

import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;

/**
 * This class stores information about the state of the SkyBlock game the player is currently in.
 */
public class GameState {
    /**
     * Whether the player is currently on SkyBlock
     */
    private static boolean onSkyBlock;
    /**
     * The current stage of the in-game season, can be "Early", mid (null), or "Late".
     */
    private static String seasonStage;
    /**
     * The current in-game season
     */
    private static String season;
    /**
     * Current in-game menu the player has open
     */
    private static Menu currentMenu;

    public static boolean isOnSkyBlock() {
        return onSkyBlock || Settings.shouldSkipSkyBlockCheck();
    }

    public static void setOnSkyBlock(boolean onSkyBlock) {
        GameState.onSkyBlock = onSkyBlock;
    }

    public static String getSeasonStage() {
        return seasonStage;
    }

    public static void setSeasonStage(String seasonStage) {
        GameState.seasonStage = seasonStage;
    }

    public static String getSeason() {
        return season;
    }

    public static void setSeason(String season) {
        GameState.season = season;
    }

    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public static void setCurrentMenu(Menu currentMenu) {
        GameState.currentMenu = currentMenu;
    }
}
