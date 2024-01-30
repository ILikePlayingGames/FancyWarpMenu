/*
 * Copyright (c) 2024. TirelessTraveler
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

package ca.tirelesstraveler.fancywarpmenu.utils;

import ca.tirelesstraveler.fancywarpmenu.FancyWarpMenu;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.Menu;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.matchconditions.ItemMatchCondition;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.matchconditions.MenuMatchCondition;
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.matchconditions.MenuNameMatchCondition;
import ca.tirelesstraveler.fancywarpmenu.state.GameState;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains utility methods that determine the values to save in {@link ca.tirelesstraveler.fancywarpmenu.state.GameState}
 * from the current state of the SkyBlock server the player is in.
 */
public class GameChecks {
    private static final Logger logger = LogManager.getLogger();

    // TODO: Re-add show workshop during winter
    /**
     * Checks if the SkyBlock season is Late Winter, used for hiding Jerry's Workshop when it's closed
     */
    public static void checkLateWinter() {
        // Don't run outside of SB to prevent exceptions
        if (!Settings.shouldSkipSkyBlockCheck()) {
            try {
                Scoreboard sb = Minecraft.getMinecraft().theWorld.getScoreboard();
                // SkyBlock sidebar objective
                ArrayList<Score> scores = (ArrayList<Score>) sb.getSortedScores(sb.getObjective("SBScoreboard"));

                // The date is always near the top (highest score) so we iterate backwards.
                for (int i = scores.size(); i > 0; i--) {
                    Score score = scores.get(i - 1);
                    String playerNameDisplayFormat = sb.getPlayersTeam(score.getPlayerName()).formatString("");

                    if (playerNameDisplayFormat.trim().startsWith("Late Winter")) {
                        GameState.setLateWinter(true);
                        return;
                    }
                }

                GameState.setLateWinter(false);
            } catch (RuntimeException e) {
                logger.warn("Failed to check scoreboard season for late winter", e);
            }
        }
    }

    /**
     * Determines which SkyBlock {@code GuiChest} menu the player is in using
     * {@link ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.matchconditions.MenuMatchCondition}s, saves
     * the determined {@code Menu} to {@link ca.tirelesstraveler.fancywarpmenu.state.GameState}, and also returns it.
     *
     * @param chestInventory the inventory of the chest holding the menu
     * @param titleOnly check only the chest title, used for initial checks when the items haven't loaded in yet
     * @return a {@code Menu} value representing the current menu the player has open
     */
    public static Menu determineOpenMenu(IInventory chestInventory, boolean titleOnly) {
        Menu matchedMenu = Menu.NONE;

        if (chestInventory.hasCustomName()) {
            String chestTitle = chestInventory.getDisplayName().getUnformattedText();

            for (Map.Entry<Menu, List<MenuMatchCondition>> menuMatchingEntry :
                    FancyWarpMenu.getSkyBlockConstants().getMenuMatchingMap().entrySet()) {
                List<MenuMatchCondition> matchConditions = menuMatchingEntry.getValue();
                boolean conditionFailed = false;

                for (MenuMatchCondition matchCondition : matchConditions) {
                    if (matchCondition.getClass().equals(MenuNameMatchCondition.class)) {
                        if (!((MenuNameMatchCondition) matchCondition).menuNameMatches(chestTitle)) {
                            conditionFailed = true;
                            break;
                        }
                    } else if (!titleOnly && matchCondition.getClass().equals(ItemMatchCondition.class)) {
                        if (!((ItemMatchCondition) matchCondition).inventoryContainsMatchingItem(chestInventory)) {
                            conditionFailed = true;
                            break;
                        }
                    }
                }

                if (!conditionFailed) {
                    matchedMenu = menuMatchingEntry.getKey();
                }
            }
        }

        GameState.setCurrentMenu(matchedMenu);
        return matchedMenu;
    }
}
