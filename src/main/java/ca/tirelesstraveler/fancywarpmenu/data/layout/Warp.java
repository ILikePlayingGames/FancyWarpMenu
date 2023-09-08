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

package ca.tirelesstraveler.fancywarpmenu.data.layout;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import static ca.tirelesstraveler.fancywarpmenu.data.DataCommon.gson;

/**
 * Warp entry data used to create the warp buttons on the GUI
 */
@SuppressWarnings("unused")
public class Warp {
    // Height scale is the same as width
    /** Grid unit width is islandWidth / widthFactor */
    public static int GRID_UNIT_WIDTH_FACTOR = 40;
    /** Warp button texture, shared between all warp buttons */
    public static WarpIcon warpIcon;
    /** Warp button width in pixels, see {@link this#initDefaults(ScaledResolution)} */
    private static int width;
    /** Warp button height in pixels, see {@link this#initDefaults(ScaledResolution)} */
    private static int height;
    /** x-coordinate to draw the warp button at (0-40) */
    private int gridX;
    /** y-coordinate to draw the warp button at (0-40) */
    private int gridY;
    /** Name of the warp, rendered below the warp button texture */
    private String displayName;
    /** Name of the warp as used in the {@code /warp} command */
    private String commandName;
    /** Index of the inventory slot corresponding to this warp in a warp {@code GuiChest} */
    private int slotIndex;
    /** Don't draw the display name under the warp's button */
    private boolean hideDisplayName;
    /** Warps only usable during special game modes such as Bingo */
    private boolean requiresSpecialGameMode;
    /** Skips drawing the warp button, useful for islands with only one warp */
    private boolean hideButton;

    private Warp() {
    }

    public String getDisplayName() {
        return hideDisplayName ? "" : displayName;
    }

    public ResourceLocation getWarpTextureLocation() {
        return warpIcon.getTextureLocation();
    }

    public ResourceLocation getWarpHoverEffectTextureLocation() {
        return warpIcon.getHoverEffectTextureLocation();
    }

    /**
     * Returns the command the player has to send to use this warp.
     * If the {@code commandName} doesn't start with a '/', "/warp " is prepended.
     */
    public String getWarpCommand() {
        // hardcoded to prevent command injection
        return commandName.equals("/savethejerrys") ? commandName : "/warp " + commandName;
    }

    public int getSlotIndex() {
        return slotIndex;
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

    public boolean requiresSpecialGameMode() {
        return requiresSpecialGameMode;
    }

    public boolean shouldHideButton() {
        return hideButton;
    }

    public String toString() {
        return gson.toJson(this);
    }

    /**
     * Initializes width and height for all warp buttons.
     * This should be called in {@link GuiScreen#initGui()}.
     */
    public static void initDefaults(ScaledResolution res) {
        float scaleFactor;
        width = (int) (res.getScaledWidth() * warpIcon.getWidthPercentage());
        scaleFactor = (float) width / warpIcon.getTextureWidth();
        height = (int) (warpIcon.getTextureHeight() * scaleFactor);
    }

    public void setHideDisplayName(boolean hideDisplayName) {
        this.hideDisplayName = hideDisplayName;
    }

    public static void setWarpIcon(WarpIcon warpIcon) {
        Warp.warpIcon = warpIcon;
    }

    public static void validateWarp(Warp warp) throws IllegalArgumentException, NullPointerException {
        if (warp == null) {
            throw new NullPointerException("Warp cannot be null");
        }

        String name = warp.displayName;
        if (name == null) {
            throw new IllegalArgumentException(String.format("The following warp lacks a name: %s", warp));
        }

        if (warp.commandName == null || warp.commandName.isEmpty()) {
            throw new IllegalArgumentException(String.format("Warp %s is missing a command name", warp.displayName));
        }

        if (warp.gridX < 0 || warp.gridX > GRID_UNIT_WIDTH_FACTOR) {
            throw new IllegalArgumentException(String.format("Warp %s gridX is outside island", name));
        }

        if (warp.gridY < 0 || warp.gridY > GRID_UNIT_WIDTH_FACTOR) {
            throw new IllegalArgumentException(String.format("Warp %s gridY is outside island", name));
        }
    }
}
