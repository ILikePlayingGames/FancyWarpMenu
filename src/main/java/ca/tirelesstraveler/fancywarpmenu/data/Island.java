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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;

/**
 * Island data used to create the island buttons on the GUI
 */
@SuppressWarnings("unused")
public class Island {
    /** Grid unit width is screenWidth / widthFactor */
    public static final int GRID_UNIT_WIDTH_FACTOR = 64;
    /** Grid unit height is screenHeight / heightFactor */
    public static final int GRID_UNIT_HEIGHT_FACTOR = 36;

    /** Island name to be displayed below the island button*/
    private String name;
    /** Path to texture relative to {@code resources/assets/fancywarpmenu} */
    private String texturePath;
    /** Path to texture relative to {@code resources/assets/fancywarpmenu}, drawn when button is hovered */
    private String hoverEffectTexturePath;
    /** x-coordinate to draw island button at (0-64) */
    private int gridX;
    /** y-coordinate to draw island button at (0-36) */
    private int gridY;
    /** z-coordinate to draw island button at (0-9) */
    private int zLevel;
    /** Width to render the island texture, given as a percentage of total screen width. Texture height is set automatically. */
    private float widthPercentage;
    /** List of warps to draw as buttons above the island */
    private List<Warp> warpList;
    private transient ResourceLocation textureLocation;
    private transient ResourceLocation hoverEffectTextureLocation;
    private transient int textureWidth;
    private transient int textureHeight;
    private transient int width;
    private transient int height;

    private Island() {
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public ResourceLocation getHoverEffectTextureLocation() {
        return hoverEffectTextureLocation;
    }

    public String getHoverEffectTexturePath() {
        return hoverEffectTexturePath;
    }

    public List<Warp> getWarps() {
        return warpList;
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

    public int getzLevel() {
        return zLevel;
    }

    public int getWarpCount() {
        return warpList.size();
    }

    public void init(ScaledResolution res) {
        float scaleFactor;
        width = (int) (res.getScaledWidth() * widthPercentage);
        scaleFactor = (float) width / textureWidth;
        height = (int) (textureHeight * scaleFactor);
    }

    public void setTextureDimensions(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void setTextureLocation() {
        textureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), texturePath);
    }

    public void setHoverEffectTextureLocation() {
        hoverEffectTextureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), hoverEffectTexturePath);
    }

    public String toString() {
        return WarpConfiguration.gson.toJson(this);
    }

    public static void validateIsland(Island island) throws IllegalArgumentException, NullPointerException {
        if (island == null) {
            throw new NullPointerException("Island cannot be null");
        }

        String name = island.name;

        if (name == null) {
            throw new IllegalArgumentException(String.format("The following island lacks a name: %s", island));
        }

        if (island.texturePath == null) {
            throw new NullPointerException("Island texture path cannot be null");
        }

        ResourceLocation textureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), island.texturePath);
        try {
            Minecraft.getMinecraft().getResourceManager().getResource(textureLocation);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Island %s texture not found at %s", name, textureLocation));
        }

        if (island.hoverEffectTextureLocation != null) {
            textureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), island.hoverEffectTexturePath);
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(textureLocation);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Island %s hover effect texture not found at %s", name, textureLocation));
            }
        }

        if (island.gridX < 0 || island.gridX > GRID_UNIT_WIDTH_FACTOR) {
            throw new IllegalArgumentException(String.format("Island %s gridX is outside screen", name));
        }

        if (island.gridY < 0 || island.gridY > GRID_UNIT_HEIGHT_FACTOR) {
            throw new IllegalArgumentException(String.format("Island %s gridY is outside screen", name));
        }

        if (island.zLevel < 0) {
            throw new IllegalArgumentException(String.format("Island %s zLevel is outside screen", name));
        } else if (island.zLevel >= 10) {
            throw new IllegalArgumentException(String.format("Island %s zLevel is too high. Z levels 10+ are reserved for warp buttons.", name));
        }

        if (island.widthPercentage < 0 || island.widthPercentage > 1) {
            throw new IllegalArgumentException(String.format("Island %s widthPercentage must be between 0 and 1", name));
        }

        if (island.warpList == null || island.warpList.isEmpty()) {
            throw new IllegalArgumentException(String.format("Island %s has no warps", name));
        }

        for (Warp warp:
                island.warpList) {
            Warp.validateWarp(warp);
        }
    }
}
