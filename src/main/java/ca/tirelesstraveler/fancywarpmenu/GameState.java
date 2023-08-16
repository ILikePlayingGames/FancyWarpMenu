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

/**
 * This class stores the state of the SkyBlock server the player is on. This information is used for the conditional hiding of warps.
 */
public class GameState {
    /**
     * The team name of the scoreboard team that holds the SkyBlock date
     */
    public static String DATE_TEAM_INDEX = "team_8";

    private static boolean onSkyBlock;
    /**
     * Whether the current SkyBlock season is Late Winter
     */
    private static boolean lateWinter;

    public static boolean isOnSkyBlock() {
        return onSkyBlock;
    }

    public static void setOnSkyBlock(boolean onSkyBlock) {
        GameState.onSkyBlock = onSkyBlock;
    }

    public static boolean isLateWinter() {
        return lateWinter;
    }

    public static void setLateWinter(boolean lateWinter) {
        GameState.lateWinter = lateWinter;
    }
}
