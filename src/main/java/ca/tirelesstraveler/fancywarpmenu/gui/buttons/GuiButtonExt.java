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

package ca.tirelesstraveler.fancywarpmenu.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Button class with additional utility methods
 */
public abstract class GuiButtonExt extends GuiButton implements Comparable<GuiButtonExt> {
    protected ResourceLocation backgroundTextureLocation;
    protected ResourceLocation foregroundTextureLocation;

    /**
     * Constructor without coordinates for when placement is set at runtime
     */
    public GuiButtonExt(int buttonId, String buttonText) {
        this(buttonId, 0, 0, buttonText);
    }
    public GuiButtonExt(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    /**
     * Calculates whether this button is hovered instead of drawing a vanilla button
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        }
    }

    /**
     * Draw the button's display string (label).
     */
    public void drawDisplayString(float x, float y) {
        // White
        drawDisplayString(x, y, 14737632);
    }

    /**
     * Draw the button's display string (label).
     *
     * @param x x-coordinate to center the string on
     * @param y y-coordinate to center the string on
     * @param rgb a colour in the integer rgb format produced by {@link Color#getRGB()}
     */
    public void drawDisplayString(float x, float y, int rgb) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        String[] lines = displayString.split("\n", 3);

        for (int i = 0; i < lines.length; i++) {
            fontRenderer.drawStringWithShadow(displayString, x - (float) fontRenderer.getStringWidth(displayString) / 2, y + (10 * i), rgb);
        }
    }

    /**
     * Compares the z-level of this {@code GuiButtonExt} with another {@code GuiButtonExt}
     * Returns negative if this button's z-level is smaller than the other button's, 0 if their z-levels are equal,
     * and positive if this button's z-level is greater than the other button's.
     *
     * @param o the object to be compared.
     */
    @Override
    public int compareTo(@NotNull GuiButtonExt o) {
        return (int) (this.zLevel - o.zLevel);
    }

    public int getZLevel() {
        return (int) zLevel;
    }
}
