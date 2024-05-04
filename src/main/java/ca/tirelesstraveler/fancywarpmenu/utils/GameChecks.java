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
import ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.menu.ItemMatchCondition;
import ca.tirelesstraveler.fancywarpmenu.state.GameState;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains utility methods that determine the values to save in {@link ca.tirelesstraveler.fancywarpmenu.state.GameState}
 * from the current state of the SkyBlock server the player is in.
 */
public class GameChecks {
    private static final Logger logger = LogManager.getLogger();
    private static final Matcher seasonMatcher =
            Pattern.compile("(?<seasonStage>Late|Early)? ?(?<season>[a-zA-Z]+) \\d{1,2}.*").matcher("");

    /**
     * Checks the current SkyBlock season and saves using {@link GameState#setSeason(String)}.
     */
    public static void checkSeason() {
        // Don't run outside of SB to prevent exceptions
        if (!Settings.shouldSkipSkyBlockCheck()) {
            try {
                Scoreboard sb = Minecraft.getMinecraft().theWorld.getScoreboard();
                // SkyBlock sidebar objective
                ArrayList<Score> scores = (ArrayList<Score>) sb.getSortedScores(sb.getObjectiveInDisplaySlot(1));

                // The date is always near the top (highest score) so we iterate backwards.
                for (int i = scores.size(); i > 0; i--) {
                    Score score = scores.get(i - 1);
                    String skyBlockScoreboardLine =
                            sb.getPlayersTeam(score.getPlayerName()).formatString("").trim();

                    seasonMatcher.reset(skyBlockScoreboardLine);

                    if (seasonMatcher.matches()) {
                        String seasonStage = seasonMatcher.group("seasonStage");
                        String season = seasonMatcher.group("season");

                        GameState.setSeasonStage(seasonStage);

                        if (season != null) {
                            GameState.setSeason(season);
                            return;
                        }
                    }
                }

                GameState.setSeasonStage(null);
                GameState.setSeason(null);
            } catch (RuntimeException e) {
                logger.warn("Failed to check scoreboard season", e);
            }
        }
    }

    /**
     * Determines which SkyBlock {@code GuiChest} menu the player is in using the {@link net.minecraft.client.gui.inventory.GuiChest}
     * display name. This is used for initial checks when the items haven't loaded in yet.
     *
     * @param chestInventory the inventory of the chest holding the menu
     * @return a {@code Menu} value representing the current menu the player has open
     */
    public static Menu determineOpenMenu(IInventory chestInventory) {
        if (chestInventory.hasCustomName()) {
            String chestTitle = chestInventory.getDisplayName().getUnformattedText();

            for (Menu menu : FancyWarpMenu.getSkyBlockConstants().getMenuMatchingMap().keySet()) {
                if (chestTitle.equals(menu.getMenuDisplayName())) {
                    return menu;
                }
            }
        }
        
        return Menu.NONE;
    }

    /**
     * Determines if the player is in the given menu by checking whether all the {@link ItemMatchCondition}s for that menu
     * match the given inventory. This should be used after the inventory has loaded the slot index returned by
     * {@link ca.tirelesstraveler.fancywarpmenu.data.skyblockconstants.SkyBlockConstants#getLastMatchConditionInventorySlotIndex(Menu)}.
     * If a match is found, the matched menu is saved using {@link GameState#setCurrentMenu(Menu)}
     *
     * @param menu the {@code Menu} whose match conditions will be checked
     * @param chestInventory the inventory to check against the match conditions
     * @return {@code true} if all the {@link ItemMatchCondition}s match, {@code false} otherwise
     */
    public static boolean menuItemsMatch(Menu menu, IInventory chestInventory) {
        List<ItemMatchCondition> matchConditions = FancyWarpMenu.getSkyBlockConstants().getMenuMatchingMap().get(menu);

        for (ItemMatchCondition matchCondition : matchConditions) {
            logger.debug("Starting item match on slot {} for menu {}.",
                    matchCondition.getInventorySlotIndex(), menu);

            if (matchCondition.inventoryContainsMatchingItem(chestInventory)) {
                logger.warn("Item match on slot {} failed.", matchCondition.getInventorySlotIndex());
                GameState.setCurrentMenu(Menu.NONE);
                return false;
            }
            logger.debug("Finished item match on slot {} for menu {}.",
                    matchCondition.getInventorySlotIndex(), menu);
        }

        GameState.setCurrentMenu(menu);
        return true;
    }
}
