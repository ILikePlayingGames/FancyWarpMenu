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

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

/**
 * Warp entry data used to create the warp buttons on the GUI
 */
@SuppressWarnings("unused")
public class Warp {
    // Height scale is the same as width
    /** Grid unit width is islandWidth / widthFactor */
    public static float GRID_UNIT_WIDTH_FACTOR = 40;
    public static WarpIcon warpIcon;
    private static int width;
    private static int height;
    private int gridX;
    private int gridY;
    private String displayName;
    private String commandName;

    private Warp() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public ResourceLocation getWarpTextureLocation() {
        return warpIcon.getTextureLocation();
    }

    /**
     * Returns the command the player has to send to use this warp.
     * If the {@code commandName} doesn't start with a '/', "/warp " is prepended.
     */
    public String getWarpCommand() {
        // hardcoded to prevent command injection
        return commandName.equals("/savethejerrys") ? commandName : "/warp " + commandName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    /**
     * Initializes width and height for all warp buttons
     */
    public static void initDefaults(ScaledResolution res) {
        calculateAndSetWidth(res);
        calculateAndSetHeight();
    }

    public static void setWarpIcon(WarpIcon warpIcon) {
        Warp.warpIcon = warpIcon;
    }

    private static void calculateAndSetWidth(ScaledResolution res) {
        width = (int) (res.getScaledWidth() * warpIcon.getWidthPercentage());
    }

    private static void calculateAndSetHeight() {
        height = (int) (width * warpIcon.getHeightPercentage());
    }
}
