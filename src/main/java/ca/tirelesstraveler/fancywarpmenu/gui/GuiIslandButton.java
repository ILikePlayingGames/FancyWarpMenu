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

package ca.tirelesstraveler.fancywarpmenu.gui;

import ca.tirelesstraveler.fancywarpmenu.data.Island;
import ca.tirelesstraveler.fancywarpmenu.data.Settings;
import ca.tirelesstraveler.fancywarpmenu.data.Warp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class GuiIslandButton extends GuiButtonExt {
    private final Island island;
    // The width of the grid squares on which to place the warps
    private final float GRID_UNIT_WIDTH;

    public GuiIslandButton(GuiFancyWarp parent, int buttonId, ScaledResolution res, Island island) {
        super(buttonId, "", res);
        this.island = island;
        island.init(res);
        xPosition = parent.getActualX(island.getGridX());
        yPosition = parent.getActualY(island.getGridY());
        width = island.getWidth();
        height = island.getHeight();
        GRID_UNIT_WIDTH = width / Warp.GRID_UNIT_WIDTH_FACTOR;
        displayString = EnumChatFormatting.GREEN + island.getName();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY <yPosition + height;
            mc.getTextureManager().bindTexture(island.getTextureLocation());
            if (hovered) {
                GlStateManager.color(1, 1, 1, 1);
            } else {
                GlStateManager.color(0.85F, 0.85F, 0.85F, 1F);
            }
            drawScaledCustomSizeModalRect(xPosition, yPosition, 0, 0, 1, 1, island.getWidth(), island.getHeight(), 1, 1);
            GlStateManager.resetColor();

            if (Settings.shouldShowIslandLabels()) {
                drawDisplayString( xPosition + width / 2 + 1, yPosition + height + 1);
            }
        }
    }
    int findNearestGridX(int mouseX) {
        float quotient = (mouseX - xPosition) / GRID_UNIT_WIDTH;
        float remainder = (mouseX - xPosition) % GRID_UNIT_WIDTH;

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > GRID_UNIT_WIDTH / 2 ? quotient + 1 : quotient);
    }

    int findNearestGridY(int mouseY) {
        float quotient = (mouseY - yPosition) / GRID_UNIT_WIDTH;
        float remainder = (mouseY - yPosition) % GRID_UNIT_WIDTH;

        // Truncate instead of rounding to keep the point left of the cursor
        return (int) (remainder > GRID_UNIT_WIDTH / 2 ? quotient + 1 : quotient);
    }

    int getActualX(int gridX) {
        return Math.round(xPosition + GRID_UNIT_WIDTH * gridX);
    }

    int getActualY(int gridY) {
        return Math.round(yPosition + GRID_UNIT_WIDTH * gridY);
    }
}
