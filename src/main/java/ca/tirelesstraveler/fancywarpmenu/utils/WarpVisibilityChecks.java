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

import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Island;
import ca.tirelesstraveler.fancywarpmenu.data.layout.Warp;
import ca.tirelesstraveler.fancywarpmenu.state.GameState;

import java.util.List;

/**
 * Checks used to control the visibility of warps on the fancy warp menu
 */
public class WarpVisibilityChecks {

    /**
     * Checks if the given island with a singular warp should be shown on the fancy warp menu. Throws
     * {@link IllegalArgumentException} if the island has multiple warps.
     *
     * @param island the island to check
     * @return {@code true} if the island should be visible, {@code false} if it should be hidden
     */
    public static boolean shouldShowSingleWarpIsland(Island island) {
        if (island.getWarpCount() > 1) {
            throw new IllegalArgumentException("Island has more than one warp");
        }

        return shouldShowWarp(island.getWarps().get(0));
    }

    /**
     * Checks if a warp should be shown on the fancy warp menu.
     *
     * @param warp the warp to check
     * @return {@code true} if the warp should be visible, {@code false} if it should be hidden
     */
    public static boolean shouldShowWarp(Warp warp) {
        List<String> warpTags = warp.getTags();

        if (warpTags != null && !warpTags.isEmpty()) {
            for (String tag : warpTags) {
                if (!shouldShowWarpWithTag(tag)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if warps with the given tag should be shown on the fancy warp menu
     *
     * @param tag the categorization tag to check
     * @return {@code true} if warps with this tag should be visible, {@code false} if they should be hidden
     */
    private static boolean shouldShowWarpWithTag(String tag) {
        switch (tag) {
            case "bingo":
                return !Settings.shouldHideUnobtainableWarps();
            case "jerry":
                if (Settings.isDebugModeEnabled() && Settings.shouldAlwaysShowJerryIsland()) {
                    return true;
                }

                if (!Settings.shouldShowJerryIsland()) {
                    return false;
                }

                String season = GameState.getSeason();
                String seasonStage = GameState.getSeasonStage();

                return season != null && seasonStage != null && season.equals("Winter") && seasonStage.equals("Late");
            default:
                return true;
        }
    }
}
