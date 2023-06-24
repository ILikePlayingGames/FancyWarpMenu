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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Island data used to create the island buttons on the GUI
 */
@SuppressWarnings("unused")
public class Island {
    /** Grid unit width is screenWidth / widthFactor */
    public static final float GRID_UNIT_WIDTH_FACTOR = 32;
    /** Grid unit height is screenHeight / heightFactor */
    public static final float GRID_UNIT_HEIGHT_FACTOR = 18;

    private String name;
    private String texturePath;
    private int gridX;
    private int gridY;
    /** Width to render the island texture, given as a percentage of total screen width */
    private float widthPercentage;
    /** Height to render the island texture, given as a percentage of island texture width */
    private float heightPercentage;
    private List<Warp> warpList;
    private transient ResourceLocation textureLocation;
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

    public void init(ScaledResolution res) {
        calculateAndSetDimensions(res);
    }

    public void setTextureLocation() {
        textureLocation = new ResourceLocation(FancyWarpMenu.getInstance().getModId(), texturePath);
    }

    private void calculateAndSetDimensions(ScaledResolution res) {
        width = (int) (res.getScaledWidth() * widthPercentage);
        height = (int) (width * heightPercentage);
    }
}
