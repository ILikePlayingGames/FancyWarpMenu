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

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class GuiIslandButton extends GuiButtonExt {
    private final Island island;
    private final ScaledGrid scaledGrid;

    public GuiIslandButton(GuiFancyWarp parent, int buttonId, ScaledResolution res, Island island) {
        super(buttonId, "");
        this.island = island;
        island.init(res);
        xPosition = parent.getActualX(island.getGridX());
        yPosition = parent.getActualY(island.getGridY());
        zLevel = island.getzLevel();
        width = island.getWidth();
        height = island.getHeight();
        scaledGrid = new ScaledGrid(xPosition, yPosition, width / Warp.GRID_UNIT_WIDTH_FACTOR);
        displayString = EnumChatFormatting.GREEN + island.getName();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(island.getTextureLocation());
            GlStateManager.enableBlend();
            if (hovered) {
                GlStateManager.color(1F, 1F, 1F);
            } else {
                GlStateManager.color(0.5F, 0.5F, 0.5F);
            }
            // Blend allows the texture to be drawn with transparency intact
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, zLevel);
            drawScaledCustomSizeModalRect(scaledGrid.getScaledPosition(xPosition), scaledGrid.getScaledPosition(yPosition), 0, 0, 1, 1, scaledGrid.getScaledDimension(island.getWidth()), scaledGrid.getScaledDimension(island.getHeight()), 1, 1);
            GlStateManager.disableBlend();
            GlStateManager.resetColor();

            if (Settings.shouldShowIslandLabels()) {
                drawDisplayString(scaledGrid.getScaledPosition(xPosition + width / 2 + 1), scaledGrid.getScaledPosition(yPosition + height + 1));
            }
            GlStateManager.popMatrix();
        }
    }

    public Island getIsland() {
        return island;
    }

    int findNearestGridX(int mouseX) {
        return scaledGrid.findNearestGridX(mouseX);
    }

    int findNearestGridY(int mouseY) {
        return scaledGrid.findNearestGridY(mouseY);
    }

    int getActualX(int gridX) {
        return scaledGrid.getActualX(gridX);
    }

    int getActualY(int gridY) {
        return scaledGrid.getActualY(gridY);
    }
}
