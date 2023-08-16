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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Class that holds the settings for drawing the config button that opens the mod's settings
 */
@SuppressWarnings("unused")
public class ConfigButton {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), "textures/gui/Logo.png");
    /** Overlay texture rendered when mod is outdated */
    public static final ResourceLocation NOTIFICATION_TEXTURE_LOCATION = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), "textures/gui/Notification.png");

    /** x-coordinate on {@link ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp} to draw the config button at (0-{@link Island#GRID_UNIT_WIDTH_FACTOR}) */
    private int gridX;
    /** y-coordinate on {@link ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp} to draw the config button at (0-{@link Island#GRID_UNIT_HEIGHT_FACTOR}) */
    private int gridY;
    /** Width to render the config button texture at as a percentage of the screen width. Texture height is set automatically. */
    private float widthPercentage;
    /** Width of the warp icon texture, used to set the width of the warp button */
    private transient int textureWidth;
    /** Height of the warp icon texture, used to set the height of the warp button */
    private transient int textureHeight;
    /** Width of the config button */
    private transient int width;
    /** Height of the config button */
    private transient int height;

    private ConfigButton(){}

    public ResourceLocation getTextureLocation() {
        return TEXTURE_LOCATION;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public float getWidthPercentage() {
        return widthPercentage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Initialize config button width and height.
     * This should be called in {@link GuiScreen#initGui()}.
     */
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

    public String toString() {
        return Layout.gson.toJson(this);
    }

    public static void validateConfigButtonIcon(ConfigButton configButton) throws IllegalArgumentException, NullPointerException {
        if (configButton == null) {
            throw new NullPointerException("Config button cannot be null");
        }

        try {
            Minecraft.getMinecraft().getResourceManager().getResource(ConfigButton.TEXTURE_LOCATION);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Config button texture not found at %s", ConfigButton.TEXTURE_LOCATION));
        }

        try {
            Minecraft.getMinecraft().getResourceManager().getResource(ConfigButton.NOTIFICATION_TEXTURE_LOCATION);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Config button notification texture not found at %s", ConfigButton.NOTIFICATION_TEXTURE_LOCATION));
        }

        if (configButton.gridX < 0 || configButton.gridX > Island.GRID_UNIT_WIDTH_FACTOR) {
            throw new IllegalArgumentException("Config button gridX must be between 0 and " + Island.GRID_UNIT_WIDTH_FACTOR + " inclusive");
        }

        if (configButton.gridY < 0 || configButton.gridY > Island.GRID_UNIT_HEIGHT_FACTOR) {
            throw new IllegalArgumentException("Config button gridX must be between 0 and " + Island.GRID_UNIT_HEIGHT_FACTOR + " inclusive");
        }

        if (configButton.widthPercentage < 0 || configButton.widthPercentage > 1) {
            throw new IllegalArgumentException("Config button icon widthPercentage must be between 0 and 1");
        }
    }
}
