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

import static ca.tirelesstraveler.fancywarpmenu.resourceloaders.ResourceLoader.gson;

/**
 * Class that holds the settings for drawing buttons that are not islands, like the config button.
 * This class should not be used directly. Subclasses should provide their own textures and additional fields.
 */
@SuppressWarnings("unused")
public abstract class Button {

    /** x-coordinate on {@link ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp} to draw the button at (0-{@link Island#GRID_UNIT_WIDTH_FACTOR}) */
    private int gridX;
    /** y-coordinate on {@link ca.tirelesstraveler.fancywarpmenu.gui.GuiFancyWarp} to draw the button at (0-{@link Island#GRID_UNIT_HEIGHT_FACTOR}) */
    private int gridY;
    /** Width to render the button texture at as a percentage of the screen width. Texture height is set automatically. */
    private float widthPercentage;
    /** Width of the icon texture in pixels, used to set the width of the button */
    private transient int textureWidth;
    /** Height of the icon texture in pixels, used to set the height of the button */
    private transient int textureHeight;
    /** Width of the button in pixels */
    private transient int width;
    /** Height of the button in pixels */
    private transient int height;

    Button(){}

    public abstract ResourceLocation getTextureLocation();

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
     * Initialize button width and height.
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
        return gson.toJson(this);
    }

    public static void validateButtonIcon(Button button) throws IllegalArgumentException, NullPointerException {
        if (button.gridX < 0 || button.gridX > Island.GRID_UNIT_WIDTH_FACTOR) {
            throw new IllegalArgumentException("Button gridX must be between 0 and " + Island.GRID_UNIT_WIDTH_FACTOR + " inclusive");
        }

        if (button.gridY < 0 || button.gridY > Island.GRID_UNIT_HEIGHT_FACTOR) {
            throw new IllegalArgumentException("Button gridX must be between 0 and " + Island.GRID_UNIT_HEIGHT_FACTOR + " inclusive");
        }

        // A button width of zero causes a stack overflow
        if (button.widthPercentage <= 0 || button.widthPercentage > 1) {
            throw new IllegalArgumentException("Button icon widthPercentage must be within the interval (0,1]");
        }
    }
}
